package databuffers;

public interface ArrayCopier<T, E extends Throwable> {
	T copy(byte[] data, int offset, int length) throws E;
}
