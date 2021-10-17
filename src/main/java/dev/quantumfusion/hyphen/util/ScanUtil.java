package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.scan.annotations.IgnoreInterfaces;
import dev.quantumfusion.hyphen.scan.annotations.IgnoreSuperclass;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

	public static AnnotatedType[] append(AnnotatedType[] oldArray, AnnotatedType value) {
		final int length = oldArray.length;
		AnnotatedType[] out = new AnnotatedType[length + 1];
		System.arraycopy(oldArray, 0, out, 0, length);
		out[length] = value;
		return out;
	}

	public static Annotation[] parseAnnotations(@Nullable Clazz clazz) {
		if (clazz == null) return new Annotation[0];
		return clazz.getClassAnnotations();
	}

	public static AnnotatedType wrap(Type clazz) {
		return new AnnotatedWrapped(clazz);
	}

	public static Class<?> getClassFrom(AnnotatedType type) {
		return getClassFrom(type.getType());
	}

	public static Class<?> getClassFrom(Type type) {
		if (type instanceof Class<?> c) return c;
		if (type instanceof ParameterizedType pt) return getClassFrom(pt.getRawType());

		throw new IllegalArgumentException(type.getClass() + ": " + type);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T getAnnotation(Class<T> cls, Clazz clazz) {
		for (Annotation annotation : clazz.annotations)
			if (cls.isInstance(annotation)) return (T) annotation;
		for (Annotation annotation : clazz.parentClassAnnotations)
			if (cls.isInstance(annotation)) return (T) annotation;
		return null;
	}

	private record AnnotatedWrapped(Type type) implements AnnotatedType {

		@Override
		public Type getType() {
			return type;
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

		@Override
		public AnnotatedType getAnnotatedOwnerType() {
			return null;
		}
	}
}
