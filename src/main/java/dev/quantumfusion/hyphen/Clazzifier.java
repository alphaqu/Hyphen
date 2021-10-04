package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.thr.ScanException;
import dev.quantumfusion.hyphen.type.*;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.CacheUtil;

import java.lang.reflect.*;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Clazzifier {
	public static final Clazz UNKNOWN = new Unknown();
	private static final Map<Class<? extends Type>, BiFunction<Type, Clazz, Clazz>> FORWARD_CLAZZERS = new LinkedHashMap<>();
	private static final Map<Type, Clazz> CREATE_CACHE = new IdentityHashMap<>();
	private static final Map<Class<?>, Clazz> CLASS_CACHE = new IdentityHashMap<>();
	private static final Map<Clazz, Clazz[]> FIELD_CACHE = new IdentityHashMap<>();

	static {
		FORWARD_CLAZZERS.put(ParameterizedType.class, ParameterizedClazz::mapForward);
		FORWARD_CLAZZERS.put(Class.class, Clazz::create);
		FORWARD_CLAZZERS.put(TypeVariable.class, TypeClazz::create);
		FORWARD_CLAZZERS.put(GenericArrayType.class, ArrayClazz::create);
		FORWARD_CLAZZERS.put(WildcardType.class, (type, clazz) -> UNKNOWN);
	}

	public static Clazz create(Type type, Clazz parent) {
		if (type instanceof Class<?> clazz)
			return CacheUtil.cache(CLASS_CACHE, clazz, () -> createFromType(type, parent));
		else return createFromType(type, parent);
	}

	private static Clazz createFromType(Type type, Clazz parent) {
		try {
			for (var entry : FORWARD_CLAZZERS.entrySet()) {
				if (entry.getKey().isAssignableFrom(type.getClass())) {
					return entry.getValue().apply(type, parent);
				}
			}
		} catch (ScanException e) {
			e.parents.add(parent);
			throw e;
		}
		throw new UnsupportedOperationException(type.getClass().getSimpleName() + " is unsupported");
	}

	public static Clazz[] scanFields(Clazz clazz) {
		return CacheUtil.cache(FIELD_CACHE, clazz, () -> {
			final Clazz[] fields = ArrayUtil.map(clazz.getFields(), Clazz[]::new, (field, integer) -> Clazzifier.create(field.getGenericType(), clazz));
			final Type aSuper = clazz.getSuper();
			if (aSuper != null) {
				return ArrayUtil.combine(scanFields(create(aSuper, clazz)), fields, Clazz[]::new);
			}
			return fields;
		});
	}


}
