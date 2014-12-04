package databuffers;

import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

public interface DataSource extends Source {

	default byte readByte(){
		return (byte) read();
	}
	default boolean readBoolean(){
		return read() != 0;
	}
	default char readChar(){
		return (char)((read() << 8) | read());
	}
	default int readInt(){
		return (read() << 24) | (read() << 16) | (read() << 8) | read();
	}
	default long readLong(){
		return ((long)readInt() << 32) | (readInt() & 0xffffffffl);
	}
	default float readFloat(){
		return Float.intBitsToFloat(readInt());
	}
	default double readDouble(){
		return Double.longBitsToDouble(readLong());
	}
	default byte[] readByteArray(int length){
		byte[] b = new byte[length];
		for(int i = 0; i < length; i++){
			b[i] = readByte();
		}
		return b;
	}
	default byte[] readBytes(){
		return readByteArray(readInt());
	}
	default String readString(){
		return new String(readBytes(), StandardCharsets.UTF_8);
	}
	default String readNullableString(){
		return readBoolean() ? null : readString();
	}
	default <T extends Enum<T>> T readEnum(Class<T> clazz){
		T[] elements = EnumCache.elements(clazz);
		int i = readSized(elements.length);
		return i == elements.length ? null : elements[i];
	}
	default <T extends Enum<T>> EnumSet<T> readEnumSet(Class<T> clazz){
		T[] elements = EnumCache.elements(clazz);
		EnumSet<T> set = EnumSet.noneOf(clazz);
		if(elements.length <= 64 || readBoolean()){
			byte[] bits = readByteArray((elements.length + 7) >> 3);
			for(int i = 0; i < bits.length; i++){
				for(int j = 0; j < 8; j++){
					if(((bits[i] >> j) & 1) == 1){
						set.add(elements[i * 8 + j]);
					}
				}
			}
		} else {
			for(int i = readSized(elements.length); i > 0; i--){
				set.add(readEnum(clazz));
			}
		}
		return set;
	}
	default <T extends Enum<T>> EnumSet<T> readEnumSetPersistent(Class<T> clazz){
		T[] elements = EnumCache.elements(clazz);
		EnumSet<T> set = EnumSet.noneOf(clazz);
		for(int i = readInt(); i > 0; i--){
			set.add(elements[readInt()]);
		}
		return set;
	}
	default int readSized(int max){
		if(max < 1 << 8){
			return read();
		} else if(max < 1 << 16){
			return readChar();
		} else {
			return readInt();
		}
	}
}
