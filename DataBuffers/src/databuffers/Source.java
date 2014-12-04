package databuffers;

public interface Source {
	/**Reads and returns a byte from this Source.
	 * @return an int in the range 0 - 255 representing the unsigned value of the byte read.*/
	int read();
}
