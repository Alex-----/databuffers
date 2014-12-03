package databuffers;

import java.util.HashMap;
import java.util.Map;

interface EnumCache {
	ThreadLocal<Map<Class<? extends Enum<?>>, Enum<?>[]>> enums 
		= ThreadLocal.withInitial(HashMap::new);

	@SuppressWarnings("unchecked")
	static <T extends Enum<T>> T[] elements(Class<T> clazz){
		return (T[])enums.get().computeIfAbsent(clazz, Class::getEnumConstants);
	}
}
