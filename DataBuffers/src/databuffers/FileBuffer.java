package databuffers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.WeakHashMap;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class FileBuffer implements Sendable{
	private static final int COMPRESSION = Deflater.BEST_SPEED;
	private final byte[] data;
	private final WeakHashMap<SocketChannel, ByteBuffer> buffers;
	public final int size;

	public FileBuffer(Path path, boolean compress) throws IOException {
		buffers = new WeakHashMap<>();
		try (SeekableByteChannel sbc = Files.newByteChannel(path);
        	InputStream in = Channels.newInputStream(sbc)) {
			if(compress){
	        	int size = (int) sbc.size();
	            byte[] data = new byte[size];
	            for (int n, nread = 0; (n = in.read(data, nread, size - nread)) > 0; nread += n);
	        	ByteArrayOutputStream out = new ByteArrayOutputStream();
	        	new DataSinkBuffer(4).writeInt(data.length).send(out);
	        	DeflaterOutputStream zip = new DeflaterOutputStream(out, new Deflater(COMPRESSION));
	        	zip.write(data);
	        	zip.close();
	            this.data = out.toByteArray();
	            this.size = this.data.length - 4;
	            out = new ByteArrayOutputStream(4);
	            new DataOutputStream(out).writeInt(this.size);
	            System.arraycopy(out.toByteArray(), 0, this.data, 0, 4);
			} else {
	        	size = (int) sbc.size();
	        	ByteArrayOutputStream out = new ByteArrayOutputStream(4);
	            new DataOutputStream(out).writeInt(size);
	            byte[] s = out.toByteArray();
	            data = Arrays.copyOf(s, size + 4);
	            for (int n, nread = 0; (n = in.read(data, nread + 4, size - nread)) > 0; nread += n);
			}
        }
	}
	@Override
	public boolean send(SocketChannel out) throws IOException {
		ByteBuffer sendBuffer = buffers.computeIfAbsent(out, c -> ByteBuffer.wrap(data));
		out.write(sendBuffer);
		boolean finished = !sendBuffer.hasRemaining();
		if(finished){
			buffers.remove(out);
		}
		return finished;
	}
}
