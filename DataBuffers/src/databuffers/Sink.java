package databuffers;

public interface Sink {
	/**Writes a byte (the lower 8 bits of {@code v}) into this Sink.
	 * @return this*/
	Sink write(int v);
}
