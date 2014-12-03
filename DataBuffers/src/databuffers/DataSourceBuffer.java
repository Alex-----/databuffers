package databuffers;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.function.IntConsumer;

public class DataSourceBuffer extends SourceBuffer implements DataSource {

	public DataSourceBuffer(byte[] data){
		this(data, 0, data.length);
	}
	public DataSourceBuffer(byte[] data, int offset, int limit){
		super(data, offset, limit);
	}
	
	public DataSourceBuffer(DatagramPacket packet){
		this(packet.getData(), packet.getOffset(), packet.getOffset() + packet.getLength());
	}
	@Override
	public byte[] readByteArray(int len){
		if(len == 0) return new byte[0];
		if(len < 0) throw new IllegalArgumentException("length < 0");
		checkBounds(position);
		checkBounds(position + len - 1);
		return Arrays.copyOfRange(data, position, position += len);
	}
	@Override
	public DataSourceBuffer uncompress(){
		return (DataSourceBuffer) super.uncompress();
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
	public static DataSourceBuffer read(InputStream in, int limit, IntConsumer read)
			throws IOException {
		return (DataSourceBuffer) SourceBuffer.read(in, limit, read);
	}

	public static class Reader extends SourceBuffer.Reader {

		public Reader(){
			super(Integer.MAX_VALUE);
		}
		public Reader(int limit){
			super(limit);
		}
		
		public DataSourceBuffer read(SocketChannel in, Runnable close) throws IOException {
			return (DataSourceBuffer) super.read(in, close);
		}
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
