package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.thr.ScanException;
import dev.quantumfusion.hyphen.type.*;
import dev.quantumfusion.hyphen.util.AnnoUtil;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.CacheUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Clazzifier {
	public static final Clazz UNKNOWN = new Unknown();
	private static final Map<Class<? extends Type>, BiFunction<AnnotatedType, Clazz, Clazz>> FORWARD_CLAZZERS = new LinkedHashMap<>();
	private static final Map<Class<?>, Clazz> CLASS_CACHE = new IdentityHashMap<>();
	private static final Map<Clazz, Clazz[]> ALL_FIELD_CACHE = new IdentityHashMap<>();
	private static final Map<Class<?>, AnnotatedType[]> FIELD_CACHE = new IdentityHashMap<>();
	private static final Map<Class<?>, AnnotatedType> SUPER_CACHE = new IdentityHashMap<>();
	private static final Map<Class<?>, Map<Class<? extends Annotation>, Annotation>> GLOBAL_ANNO_CACHE = new IdentityHashMap<>();

	static {
		FORWARD_CLAZZERS.put(ParameterizedType.class, ParameterizedClazz::mapForward);
		FORWARD_CLAZZERS.put(TypeVariable.class, TypeClazz::create);
		FORWARD_CLAZZERS.put(GenericArrayType.class, ArrayClazz::create);
		FORWARD_CLAZZERS.put(WildcardType.class, (type, clazz) -> UNKNOWN);
		FORWARD_CLAZZERS.put(Class.class, Clazz::create);
	}

	public static Clazz create(AnnotatedType type, Clazz parent) {
		if (type.getType() instanceof Class<?> clazz)
			return CacheUtil.cache(CLASS_CACHE, clazz, (c) -> createFromType(type, parent));
		else return createFromType(type, parent);
	}

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

	public static Clazz[] scanFields(Clazz clazz) {
		return CacheUtil.cache(ALL_FIELD_CACHE, clazz, (c) -> {
			final Clazz[] fields = ArrayUtil.map(getGenericFields(c), Clazz[]::new, (field, integer) -> Clazzifier.create(field, c));
			final AnnotatedType aSuper = Clazzifier.getSuper(c);
			if (aSuper != null)
				return ArrayUtil.combine(scanFields(create(aSuper, c)), fields, Clazz[]::new);

			return fields;
		});
	}

	public static AnnotatedType[] getGenericFields(Clazz clazz) {
		return CacheUtil.cache(FIELD_CACHE, clazz.pullClass(), (c) -> ArrayUtil.map(c.getDeclaredFields(), AnnotatedType[]::new, (field, integer) -> field.getAnnotatedType()));
	}


	public static AnnotatedType getSuper(Clazz clazz) {
		return CacheUtil.cache(SUPER_CACHE, clazz.pullClass(), Class::getAnnotatedSuperclass);
	}

	public static Map<Class<? extends Annotation>, Annotation> getClassAnnotations(Clazz clazz) {
		return CacheUtil.cache(GLOBAL_ANNO_CACHE, clazz.pullClass(), aClass -> AnnoUtil.parseAnnotations(clazz.pullClass()));
	}

}
