package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.IgnoreInterfaces;
import dev.quantumfusion.hyphen.scan.annotations.IgnoreSuperclass;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ClassCache {
	private static final Map<Class<?>, List<FieldInfo>> FIELD_CACHE = new IdentityHashMap<>();
	private static final Map<Class<?>, AnnotatedType[]> INTERFACE_CACHE = new IdentityHashMap<>();
	private static final Map<Class<?>, AnnotatedType[]> INHERITED_CACHE = new IdentityHashMap<>();
	private static final Map<Class<?>, AnnotatedType> SUPER_CACHE = new IdentityHashMap<>();


	public static AnnotatedType[] getInherited(Class<?> clazz) {
		return cache(INHERITED_CACHE, clazz, (c) -> {
			if (c.isArray())
				return ArrayUtil.map(getInherited(clazz.componentType()), AnnotatedType[]::new, annotatedType ->
						ScanUtil.wrap(ScanUtil.getClassFrom(annotatedType).arrayType()));

			var classInterface = ClassCache.getInterfaces(clazz);
			var classSuper = ClassCache.getSuperClass(clazz);
			if (classSuper != null) return ScanUtil.append(classInterface, classSuper);
			return classInterface;
		});
	}

	public static AnnotatedType getSuperClass(Class<?> clazz) {
		return cache(SUPER_CACHE, clazz, (c) -> {
			if (clazz.getDeclaredAnnotation(IgnoreSuperclass.class) == null)
				if (c.getSuperclass() != null) return c.getAnnotatedSuperclass();
			return null;
		});
	}

	public static AnnotatedType[] getInterfaces(Class<?> clazz) {
		return cache(INTERFACE_CACHE, clazz, (c) -> {
			if (clazz.getDeclaredAnnotation(IgnoreInterfaces.class) == null)
				return clazz.getAnnotatedInterfaces();
			return new AnnotatedType[0];
		});
	}

	public static List<FieldInfo> getFields(Class<?> clazz) {
		return cache(FIELD_CACHE, clazz, (c) -> {
			var out = new ArrayList<FieldInfo>();
			var globalData = clazz.getDeclaredAnnotation(Data.class) != null;
			for (Field field : clazz.getDeclaredFields()) {
				if (globalData || field.getDeclaredAnnotation(Data.class) != null) {
					out.add(new FieldInfo(field, field.getAnnotatedType()));
				}
			}
			return out;
		});
	}

	private static <K, O> O cache(Map<K, O> cache, K key, Function<K, O> creator) {
		if (cache.containsKey(key)) return cache.get(key);
		final O apply = creator.apply(key);
		cache.put(key, apply);
		return apply;
	}

	public record FieldInfo(Field field, AnnotatedType type) {

		@Override
		public String toString() {
			return field.getName() + " : " + type;
		}
	}

}
