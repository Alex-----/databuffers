package databuffers;

public interface ArrayCopierV<NULL, E extends Throwable> extends ArrayCopier<NULL, E>{
	void copyv(byte[] data, int offset, int length) throws E;
	@Override
	default NULL copy(byte[] data, int offset, int length) throws E {
		copyv(data, offset, length);
		return null;
	}
}