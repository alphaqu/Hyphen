package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.type.Clazz;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A reflection util class. If you are doing anything Class<?> related check methods here if there are cached versions.
 */
public class ReflectionUtil {
	private static final Map<Class<?>, Map<Class<? extends Annotation>, Annotation>> ANNOTATION_CACHE = new IdentityHashMap<>();
	private static final Map<Class<?>, AnnotatedType[]> INTERFACE_CACHE = new IdentityHashMap<>();
	private static final Map<Class<?>, AnnotatedType> SUPER_CACHE = new IdentityHashMap<>();
	private static final Map<Class<?>, Field[]> FIELD_CACHE = new IdentityHashMap<>();

	public static Map<Class<? extends Annotation>, Annotation> getClassAnnotations(Clazz aClass) {
		if (aClass == null) return Map.of();
		return getClassAnnotations(aClass.pullClass());
	}


	public static AnnotatedType[] getClassInterface(Clazz aClass) {
		return getClassInterface(aClass.pullClass());
	}

	public static AnnotatedType getClassSuper(Clazz aClass) {
		return getClassSuper(aClass.pullClass());
	}

	public static Field[] getClassFields(Clazz aClass) {
		return getClassFields(aClass.pullClass());
	}

	public static Map<Class<? extends Annotation>, Annotation> getClassAnnotations(Class<?> aClass) {
		return CacheUtil.cache(ANNOTATION_CACHE, aClass, AnnoUtil::parseAnnotations);
	}

	public static AnnotatedType[] getClassInterface(Class<?> aClass) {
		return CacheUtil.cache(INTERFACE_CACHE, aClass, Class::getAnnotatedInterfaces);
	}

	public static AnnotatedType getClassSuper(Class<?> aClass) {
		return CacheUtil.cache(SUPER_CACHE, aClass, Class::getAnnotatedSuperclass);
	}

	public static Field[] getClassFields(Class<?> aClass) {
		return CacheUtil.cacheCount(FIELD_CACHE, aClass, Class::getDeclaredFields);
	}
}
