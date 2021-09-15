package net.oskarstrom.hyphen.util;

import java.util.function.Function;
import java.util.function.Predicate;

public class ScanUtils {

	public static Class<?>[] pathTo(Class<?> clazz, Predicate<? super Class<?>> matcher, Function<Class<?>, Class<?>[]> splitter, int depth) {
		if (matcher.test(clazz))
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