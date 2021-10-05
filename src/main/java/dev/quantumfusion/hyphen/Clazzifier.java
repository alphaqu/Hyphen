package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.thr.ScanException;
import dev.quantumfusion.hyphen.type.*;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.CacheUtil;
import dev.quantumfusion.hyphen.util.ReflectionUtil;

import java.lang.reflect.*;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * The main type handling class. This is where you get your fields and Hyphen scans for implementations.
 */
public class Clazzifier {
	public static final Clazz UNKNOWN = new Unknown();
	private static final Map<Class<? extends Type>, BiFunction<AnnotatedType, Clazz, Clazz>> FORWARD_CLAZZERS = new LinkedHashMap<>();
	private static final Map<Class<?>, Clazz> CLASS_CACHE = new IdentityHashMap<>();
	private static final Map<Clazz, Clazz[]> ALL_FIELD_CACHE = new IdentityHashMap<>();

	static {
		FORWARD_CLAZZERS.put(ParameterizedType.class, ParameterizedClazz::mapForward);
		FORWARD_CLAZZERS.put(TypeVariable.class, TypeClazz::create);
		FORWARD_CLAZZERS.put(GenericArrayType.class, ArrayClazz::create);
		FORWARD_CLAZZERS.put(WildcardType.class, (type, clazz) -> UNKNOWN);
		FORWARD_CLAZZERS.put(Class.class, Clazz::create);
	}

	/**
	 * This creates a {@link Clazz} from an AnnotatedType.<br> This method is cached and the actual implementations is in the {@link Clazzifier#createFromType}
	 *
	 * @param type   An {@link AnnotatedType} that you want to create a {@link Clazz} from
	 * @param parent The source of the AnnotatedType. Used for mapping Class Parameters.
	 * @return The Clazz
	 */
	public static Clazz create(AnnotatedType type, Clazz parent) {
		if (type.getType() instanceof Class<?> clazz)
			return CacheUtil.cache(CLASS_CACHE, clazz, (c) -> createFromType(type, parent));
		else return createFromType(type, parent);
	}

	/**
	 * The Actual Create method.
	 */
	private static Clazz createFromType(AnnotatedType annotated, Clazz parent) {
		final Type type = annotated.getType();
		try {
			for (var entry : FORWARD_CLAZZERS.entrySet()) {
				if (entry.getKey().isAssignableFrom(type.getClass())) {
					return entry.getValue().apply(annotated, parent);
				}
			}
		} catch (ScanException e) {
			e.parents.add(parent);
			throw e;
		}
		throw new UnsupportedOperationException(type.getClass().getSimpleName() + " is unsupported");
	}

	/**
	 * Creates Clazz for every field of a clazz.
	 *
	 * @param clazz The Clazz to scan.
	 * @return The Clazz fields.
	 */
	public static Clazz[] scanFields(Clazz clazz) {
		return CacheUtil.cache(ALL_FIELD_CACHE, clazz, (c) -> {
			final Clazz[] fields = ArrayUtil.map(ReflectionUtil.getClassFields(c), Clazz[]::new, (field, integer) -> Clazzifier.create(field.getAnnotatedType(), c));
			final AnnotatedType aSuper = ReflectionUtil.getClassSuper(c);
			if (aSuper != null)
				return ArrayUtil.combine(scanFields(create(aSuper, c)), fields, Clazz[]::new);

			return fields;
		});
	}
}
