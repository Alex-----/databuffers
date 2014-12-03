package databuffers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

public abstract class Buffer implements Sendable {
	int position, offset, limit, mark;
	private ByteBuffer sendBuffer;

	Buffer(int offset, int limit) {
		this.offset = this.position = offset;
		this.limit = limit;
	}
	public Buffer skipBytes(int bytes) {
		position += bytes;
		return this;
	}
	public Buffer rewindBytes(int bytes) {
		position -= bytes;
		return this;
	}
	public Buffer rewind() {
		position = offset;
		return this;
	}
	public Buffer toMark() {
		position = mark;
		return this;
	}
	public Buffer mark() {
		mark = position;
		return this;
	}
	final void checkBounds(int i) {
		if (i >= limit || i < offset)
			throw new IndexOutOfBoundsException("index: " + i + " offset: " + offset + " limit: "
					+ limit);
	}
	public abstract int length();
	public abstract <T, E extends Throwable> T copy(ArrayCopier<T, E> copier) throws E;
	public <E extends Throwable> void copyv(ArrayCopierV<?, E> copier) throws E {
		copy(copier);
	}
	public void send(OutputStream out) throws IOException {
		new DataOutputStream(out).writeInt(length());
		copyv(out::write);
		out.flush();
	}
	@Override
	public boolean send(SocketChannel out) throws IOException {
		if (sendBuffer == null) {
			sendBuffer = ByteBuffer.allocate(length() + 4).putInt(length());
			copy(sendBuffer::put).flip();
		}
		out.write(sendBuffer);
		return !sendBuffer.hasRemaining();
	}
	public void send(DatagramChannel channel, SocketAddress socketAddress) throws IOException {
		channel.send(copy(ByteBuffer::wrap), socketAddress);
	}
	public void send(DatagramSocket socket, InetAddress address, int port) throws IOException {
		socket.send(copy((d, o, l) -> new DatagramPacket(d, o, l, address, port)));
	}
	public void imbed(DataSink sink) {
		sink.writeInt(length());
		copy(sink::writeByteArray);
	}
	public void imbedShort(DataSink sink) {
		if (length() > Character.MAX_VALUE) {
			throw new IllegalStateException("size bigger than short");
		}
		sink.writeChar(length());
		copy(sink::writeByteArray);
	}
}
