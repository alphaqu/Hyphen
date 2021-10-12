package dev.quantumfusion.hyphen.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;

public class ScanUtil {
	public static AnnotatedElement[] findPath(AnnotatedElement root, Predicate<AnnotatedElement> matcher, Function<AnnotatedElement, AnnotatedElement[]> splitter) {
		var queue = new ArrayDeque<AnnotatedElement[]>();
		var explored = new HashSet<AnnotatedElement>();

		// Add start entry
		explored.add(root);
		queue.add(new AnnotatedElement[]{root});
		while (!queue.isEmpty()) {
			var parentPath = queue.poll();
			var parentPathLength = parentPath.length;

			// last entry is always the parent
			var parent = parentPath[parentPathLength - 1];

			// check if the current class is matching and return its path
			if (matcher.test(parent)) return parentPath;

			// iterate through children
			for (AnnotatedElement child : splitter.apply(parent)) {
				//if its already explored skip
				if (explored.contains(child)) continue;
				explored.add(child);

				// Use parents path and add itself to the last entry
				queue.add(append(parentPath, child));
			}
		}

		return null;
	}

	public static AnnotatedElement[] getInherited(AnnotatedElement type) {
		var clazz = getClassFrom(type);
		var classInterface = clazz.getAnnotatedInterfaces();
		var classSuper = clazz.getAnnotatedSuperclass();
		if (classSuper != null) return append(classInterface, classSuper);
		return classInterface;
	}

	public static AnnotatedElement[] append(AnnotatedElement[] oldArray, AnnotatedElement value) {
		final int length = oldArray.length;
		AnnotatedElement[] out = new AnnotatedElement[length + 1];
		System.arraycopy(oldArray, 0, out, 0, length);
		out[length] = value;
		return out;
	}

	public static AnnotatedElement wrap(Class<?> clazz) {
		return clazz;
	}

	public static Class<?> getClassFrom(AnnotatedElement type) {
		if (type instanceof Class<?> c) return c;

		if (type instanceof AnnotatedType annotatedType) {
			final Type t = annotatedType.getType();
			if (t instanceof Class<?> c) return c;
		}

		throw new IllegalArgumentException(type.getClass() + ": " + type);
	}
}
