package net.oskarstrom.hyphen.util;

import java.util.function.Function;

public class ScanUtils {

	public static Class<?>[] pathTo(Class<?> clazz, Function<Class<?>, Boolean> matcher, Function<Class<?>, Class<?>[]> splitter, int depth) {
		if (matcher.apply(clazz))
			return new Class[depth];

		for (Class<?> aClass : splitter.apply(clazz)) {
			Class<?>[] classes = pathTo(aClass, matcher, splitter, depth + 1);
			if (classes != null) {
				classes[depth] = aClass;
				return classes;
			}
		}

		return null;
	}
}