package databuffers;

import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

public interface DataSink extends Sink {

	@Override
	DataSink write(int v);
	default DataSink writeByte(byte b) {
		return write(b);
	}
	default DataSink writeBoolean(boolean b) {
		return write(b ? 1 : 0);
	}
	default DataSink writeChar(int c) {
		write(c >>> 8);
		write(c >>> 0);
		return this;
	}
	default DataSink writeInt(int i) {
		write(i >>> 24);
		write(i >>> 16);
		write(i >>> 8);
		write(i >>> 0);
		return this;
	}
	default DataSink writeLong(long i) {
		writeInt((int) (i >>> 32));
		writeInt((int) i);
		return this;
	}
	default DataSink writeFloat(float f) {
		return writeInt(Float.floatToRawIntBits(f));
	}
	default DataSink writeDouble(double d) {
		return writeLong(Double.doubleToRawLongBits(d));
	}
	default DataSink writeByteArray(byte[] b, int offset, int length) {
		for (int i = 0; i < length; i++) {
			write(b[i + offset]);
		}
		return this;
	}
	default DataSink writeBytes(byte[] data) {
		writeInt(data.length);
		return writeByteArray(data, 0, data.length);
	}
	default DataSink writeString(String s) {
		return writeBytes(s.getBytes(StandardCharsets.UTF_8));
	}
	default DataSink writeNullableString(String s) {
		return s == null ? writeBoolean(true) : writeBoolean(false).writeString(s);
	}
	default <T extends Enum<T>> DataSink writeEnum(T t, Class<T> clazz) {
		T[] elements = EnumCache.elements(clazz);
		return writeSized(t == null ? elements.length : t.ordinal(), elements.length);
	}
	default <T extends Enum<T>> DataSink writeEnumSet(EnumSet<T> set, Class<T> clazz) {
		T[] elements = EnumCache.elements(clazz);
		int bitsetSize = (elements.length + 7) >> 3;
		boolean useBitset = true;
		if (elements.length > 64) {
			writeBoolean(useBitset = bitsetSize < (set.size() + 1)
					* (elements.length < 1 << 8 ? 1 : elements.length < 1 << 16 ? 2 : 4));
		}
		if (useBitset) {
			byte[] bits = new byte[bitsetSize];
			set.forEach(e -> bits[e.ordinal() >> 3] |= 1 << (e.ordinal() & 7));
			writeByteArray(bits, 0, bits.length);
		} else {
			writeSized(set.size(), elements.length);
			set.forEach(e -> writeEnum(e, clazz));
		}
		return this;
	}
	default <T extends Enum<T>> DataSink writeEnumSetPersistent(EnumSet<T> set) {
		writeInt(set.size());
		set.forEach(e -> writeInt(e.ordinal()));
		return this;
	}
	default DataSink writeSized(int i, int max) {
		if (max < 1 << 8) {
			return write(i);
		} else if (max < 1 << 16) {
			return writeChar(i);
		} else {
			return writeInt(i);
		}
	}
}
