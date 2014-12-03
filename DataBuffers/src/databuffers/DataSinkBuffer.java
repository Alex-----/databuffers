package databuffers;

import java.util.Arrays;
import java.util.EnumSet;

public class DataSinkBuffer extends Buffer implements DataSink {
	private byte[] data;

	public DataSinkBuffer(){
		this(50);
	}
	public DataSinkBuffer(int initialCapacity){
		this(new byte[initialCapacity]);
	}
	public DataSinkBuffer(byte[] sink){
		this(sink, 0, sink.length);
	}
	public DataSinkBuffer(byte[] sink, int offset, int limit){
		super(offset, limit);
		data = sink;
	}

	@Override
	public DataSinkBuffer write(int v){
		resize(1);
		checkBounds(position);
		data[position++] = (byte) v;
		return this;
	}
	@Override
	public DataSinkBuffer writeByteArray(byte[] b, int offset, int length){
		if(length == 0) return this; 
		if(length < 0) throw new IllegalArgumentException("length < 0"); 
		resize(length); 
		checkBounds(position); 
		System.arraycopy(b, offset, data, position, length); 
		position += length; 
		return this;
	}
	void resize(int length){
		if(position + length > limit){
			data = Arrays.copyOf(data, limit = Math.max(position + length, limit + (limit >> 1)));
		}
	}
	@Override
	public int length(){
		return position - offset;
	}
	@Override
	public <T, E extends Throwable> T copy(ArrayCopier<T, E> copier) throws E {
		return copier.copy(data, offset, length());
	}
	public DataSinkBuffer append(Buffer b){
		b.copy(this::writeByteArray);
		return this;
	}
	public DataSourceBuffer asSource(){
		return new DataSourceBuffer(data, offset, position);
	}
	public DataSourceBuffer asSourceCopy(){
		return new DataSourceBuffer(Arrays.copyOfRange(data, offset, position));
	}
	@Override public DataSinkBuffer writeByte(byte b){ DataSink.super.writeByte(b); return this; }
	@Override public DataSinkBuffer writeString(String s){ DataSink.super.writeString(s); return this; }
	@Override public DataSinkBuffer writeNullableString(String s){ DataSink.super.writeNullableString(s); return this; }
	@Override public DataSinkBuffer writeBytes(byte[] data){ DataSink.super.writeBytes(data); return this; }
	@Override public DataSinkBuffer writeInt(int i){ DataSink.super.writeInt(i); return this; }
	@Override public DataSinkBuffer writeLong(long i){ DataSink.super.writeLong(i); return this; }
	@Override public DataSinkBuffer writeFloat(float f){ DataSink.super.writeFloat(f); return this; }
	@Override public DataSinkBuffer writeDouble(double d){ DataSink.super.writeDouble(d); return this; }
	@Override public DataSinkBuffer writeChar(int c){ DataSink.super.writeChar(c); return this; }
	@Override public DataSinkBuffer writeBoolean(boolean b){ DataSink.super.writeBoolean(b); return this; }
	@Override public <T extends Enum<T>> DataSinkBuffer writeEnum(T t, Class<T> clazz){ DataSink.super.writeEnum(t, clazz); return this; }
	@Override public <T extends Enum<T>> DataSinkBuffer writeEnumSet(EnumSet<T> set, Class<T> clazz){ DataSink.super.writeEnumSet(set, clazz); return this; }
	@Override public <T extends Enum<T>> DataSinkBuffer writeEnumSetPersistent(EnumSet<T> set){ DataSink.super.writeEnumSetPersistent(set); return this; }
	@Override public DataSinkBuffer writeSized(int i, int max){ DataSink.super.writeSized(i, max); return this; }
	@Override public DataSinkBuffer skipBytes(int bytes){ super.skipBytes(bytes); return this; }
	@Override public DataSinkBuffer rewindBytes(int bytes){ super.rewindBytes(bytes); return this; }
	@Override public DataSinkBuffer rewind(){ super.rewind(); return this; }
	@Override public DataSinkBuffer toMark(){ super.toMark(); return this; }
	@Override public DataSinkBuffer mark(){ super.mark(); return this; }
}
