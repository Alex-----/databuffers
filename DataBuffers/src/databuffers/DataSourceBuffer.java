package databuffers;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.function.IntConsumer;
import java.util.zip.InflaterInputStream;

public class DataSourceBuffer extends Buffer implements DataSource {
	private final byte[] data;

	public DataSourceBuffer(DatagramPacket packet){
		this(packet.getData(), packet.getOffset(), packet.getOffset() + packet.getLength());
	}
	public DataSourceBuffer(byte[] data){
		this(data, 0, data.length);
	}
	public DataSourceBuffer(byte[] data, int offset, int limit){
		super(offset, limit);
		if(offset < 0) throw new IllegalArgumentException("offset < 0");
		if(limit > data.length) throw new IllegalArgumentException("limit > data.length");
		if(limit < offset) throw new IllegalArgumentException("limit < offset"); 
		this.data = data;
	}
	
	@Override
	public int read(){
		checkBounds(position);
		return data[position++] & 0xFF;
	}
	@Override
	public byte[] readByteArray(int length){
		if(length == 0) return new byte[0];
		if(length < 0) throw new IllegalArgumentException("length < 0");
		checkBounds(position);
		checkBounds(position + length - 1);
		return Arrays.copyOfRange(data, position, position += length);
	}
	public DataSourceBuffer uncompress(){
		int length = readInt();
		byte[] data = new byte[length];
		try(InflaterInputStream in = new InflaterInputStream(copy(ByteArrayInputStream::new))){
			for(int n, nread = 0; (n = in.read(data, nread, length - nread)) > 0; nread += n);
			return new DataSourceBuffer(data);
		} catch(IOException e){
			throw new AssertionError(e);
		}
	}
	public static DataSourceBuffer read(InputStream in) throws IOException {
		return read(in, i -> {});
	}
	public static DataSourceBuffer read(InputStream in, IntConsumer read) throws IOException {
		return read(in, Integer.MAX_VALUE, read);
	}
	public static DataSourceBuffer read(InputStream in, int limit) throws IOException {
		return read(in, limit, i -> {});
	}
	public static DataSourceBuffer read(InputStream in, int limit, IntConsumer read) throws IOException {
		DataInputStream di = new DataInputStream(in);
		int size = di.readInt();
		if(size > limit){
			throw new IllegalStateException("size > limit");
		}
		byte[] data = new byte[size];
		for(int n = 0; n < size;){
			int count = in.read(data, n, size - n);
			if(count < 0)
				throw new EOFException();
			n += count;
			read.accept(count);
		}
		return new DataSourceBuffer(data);
	}

	public static class Reader {
		private ByteBuffer buffer = ByteBuffer.allocate(4);
		private boolean readingLength = true;
		private final int limit;

		public Reader(){
			this(Integer.MAX_VALUE);
		}
		public Reader(int limit){
			this.limit = limit;
		}
		
		public DataSourceBuffer read(SocketChannel in, Runnable close) throws IOException {
			if(in.read(buffer) == -1){
				close.run();
				return null;
			}
			if(buffer.hasRemaining()){
				return null;
			}
			if(readingLength){
				readingLength = false;
				buffer.flip();
				int size = buffer.getInt();
				if(size > limit){
					throw new IllegalStateException("size > limit");
				}
				buffer = ByteBuffer.allocate(size);
				return read(in, close);
			}
			readingLength = true;
			byte[] data = buffer.array();
			buffer = ByteBuffer.allocate(4);
			return new DataSourceBuffer(data);
		}
	}

	public byte[] buffer(){
		return data;
	}
	@Override
	public int length(){
		return limit - position;
	}
	@Override
	public <T, E extends Throwable> T copy(ArrayCopier<T, E> copier) throws E {
		return copier.copy(data, position, length());
	}
	public boolean hasNext(){
		return position < limit;
	}
	public DataSourceBuffer readImbeded(){
		return new DataSourceBuffer(readBytes());
	}
	public DataSourceBuffer readImbededShort(){
		return new DataSourceBuffer(readByteArray(readChar()));
	}
	@Override public DataSourceBuffer skipBytes(int bytes){ super.skipBytes(bytes); return this; }
	@Override public DataSourceBuffer rewindBytes(int bytes){ super.rewindBytes(bytes); return this; }
	@Override public DataSourceBuffer rewind(){ super.rewind(); return this; }
	@Override public DataSourceBuffer toMark(){ super.toMark(); return this; }
	@Override public DataSourceBuffer mark(){ super.mark(); return this; }
}
