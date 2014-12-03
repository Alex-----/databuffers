package databuffers;

import java.util.Arrays;

public class SinkBuffer extends Buffer implements Sink {
	byte[] data;

	SinkBuffer(int initialCapacity){
		super(0, initialCapacity);
		data = new byte[initialCapacity];
	}
	SinkBuffer(byte[] sink, int offset, int limit){
		super(offset, limit);
		data = sink;
	}
	
	@Override
	public SinkBuffer write(int v){
		resize(1);
		checkBounds(position);
		data[position++] = (byte) v;
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
	public SinkBuffer append(Buffer b){
		b.copy(((DataSinkBuffer) this)::writeByteArray);
		return this;
	}
	public SourceBuffer asSource(){
		return new DataSourceBuffer(data, offset, position);
	}
	public SourceBuffer asSourceCopy(){
		return new DataSourceBuffer(Arrays.copyOfRange(data, offset, position));
	}
	@Override public SinkBuffer skipBytes(int bytes){ super.skipBytes(bytes); return this; }
	@Override public SinkBuffer rewindBytes(int bytes){ super.rewindBytes(bytes); return this; }
	@Override public SinkBuffer rewind(){ super.rewind(); return this; }
	@Override public SinkBuffer toMark(){ super.toMark(); return this; }
	@Override public SinkBuffer mark(){ super.mark(); return this; }
}
