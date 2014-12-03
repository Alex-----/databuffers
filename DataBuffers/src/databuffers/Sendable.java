package databuffers;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface Sendable {
	/** Writes this to a non-blocking channel.
	 * @return {@code true} if we finished sending, {@code false} if you should call this again later*/
	boolean send(SocketChannel out) throws IOException;
}
