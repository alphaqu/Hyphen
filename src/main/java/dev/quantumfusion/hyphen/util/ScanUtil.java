package dev.quantumfusion.hyphen.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;

public class ScanUtil {
	public static AnnotatedType[] findPath(AnnotatedType root, Predicate<AnnotatedType> matcher, Function<AnnotatedType, AnnotatedType[]> splitter) {
		var queue = new ArrayDeque<AnnotatedType[]>();
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
		var clazz = getClassFrom(type);
		var classInterface = clazz.getAnnotatedInterfaces();
		var classSuper = clazz.getAnnotatedSuperclass();
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

	public static AnnotatedType wrap(Type clazz) {
		return new AnnotatedType() {
			@Override
			public Type getType() {
				return clazz;
			}

			@Override
			public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
				return null;
			}

			@Override
			public Annotation[] getAnnotations() {
				return new Annotation[0];
			}

			@Override
			public Annotation[] getDeclaredAnnotations() {
				return new Annotation[0];
			}
		};
	}

	public static Class<?> getClassFrom(AnnotatedType type) {
		if (type.getType() instanceof Class<?> c) return c;

		if (type.getType() instanceof AnnotatedType annotatedType) {
			final Type t = annotatedType.getType();
			if (t instanceof Class<?> c) return c;
		}

		throw new IllegalArgumentException(type.getClass() + ": " + type);
	}
}
