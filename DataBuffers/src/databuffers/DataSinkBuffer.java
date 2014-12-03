package databuffers;

import java.util.EnumSet;

public class DataSinkBuffer extends SinkBuffer implements DataSink {

	public DataSinkBuffer(){
		this(50);
	}
	public DataSinkBuffer(int initialCapacity){
		super(initialCapacity);
	}
	public DataSinkBuffer(byte[] sink){
		this(sink, 0, sink.length);
	}
	public DataSinkBuffer(byte[] sink, int offset, int limit){
		super(sink, offset, limit);
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
	@Override public DataSourceBuffer asSource(){ return (DataSourceBuffer) super.asSource(); }
	@Override public DataSourceBuffer asSourceCopy(){ return (DataSourceBuffer) super.asSourceCopy(); }
	@Override public DataSinkBuffer append(Buffer b){ super.append(b); return this; }
	@Override public DataSinkBuffer write(int v){ super.write(v); return this; }
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
