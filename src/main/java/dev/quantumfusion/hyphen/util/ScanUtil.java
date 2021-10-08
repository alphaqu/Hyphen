package dev.quantumfusion.hyphen.util;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Predicate;

public class ScanUtil {


	public static AnnotatedType[] findPath(AnnotatedType root, Predicate<AnnotatedType> matcher, Function<AnnotatedType, AnnotatedType[]> splitter) {
		var queue = new LinkedList<AnnotatedType[]>();
		var explored = new HashSet<AnnotatedType>();

		// Add start entry
		explored.add(root);
		queue.add(new AnnotatedType[]{root});
		while (!queue.isEmpty()) {
			var parentPath = queue.poll();
			var parentPathLength = parentPath.length;

			// last entry is always the parent
			var parent = parentPath[parentPathLength - 1];

			// check if the current class is matching and return its path
			if (matcher.test(parent)) return parentPath;

			// iterate through children
			for (AnnotatedType child : splitter.apply(parent)) {
				//if its already explored skip
				if (explored.contains(child)) continue;
				explored.add(child);

				// Use parents path and add itself to the last entry
				queue.add(append(parentPath, child));
			}
		}

		return null;
	}

	public static AnnotatedType[] getInherited(AnnotatedType type) {
		final Class<?> clazz = getClassFrom(type);
		final AnnotatedType[] classInterface = ReflectionUtil.getClassInterface(clazz);
		final AnnotatedType classSuper = ReflectionUtil.getClassSuper(clazz);
		if (classSuper != null) return append(classInterface, classSuper);
		return classInterface;
	}

	public static AnnotatedType[] append(AnnotatedType[] oldArray, AnnotatedType value) {
		final int length = oldArray.length;
		AnnotatedType[] out = new AnnotatedType[length + 1];
		System.arraycopy(oldArray, 0, out, 0, length);
		out[length] = value;
		return out;
	}

	public static Class<?> getClassFrom(Type type) {
		if (type instanceof Class<?> c) return c;
		if (type instanceof ParameterizedType pt) return getClassFrom(pt.getRawType());
		if (type instanceof GenericArrayType gat) return getClassFrom(gat.getGenericComponentType());
		throw new IllegalArgumentException(type.getClass() + ": " + type);
	}

	public static Class<?> getClassFrom(AnnotatedType type) {
		return getClassFrom(type.getType());
	}
}
