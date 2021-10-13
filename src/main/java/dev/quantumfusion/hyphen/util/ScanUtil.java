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
		if (clazz instanceof ParameterizedType parameterizedType)
			return new AnnotatedParameterizedTypeWrap(parameterizedType);
		if (clazz instanceof TypeVariable typeVariable) return new AnnotatedTypeVariableWrap(typeVariable);
		if (clazz instanceof GenericArrayType genericArrayType) return new AnnotatedArrayTypeWrap(genericArrayType);
		if (clazz instanceof Class<?> c) if (c.isArray()) return new AnnotatedArrayTypeWrap(c);

		return new AnnotatedWrapped(clazz);
	}

	private static class AnnotatedParameterizedTypeWrap extends AnnotatedWrapped implements AnnotatedParameterizedType {
		private final AnnotatedType[] annotatedTypes;

		public AnnotatedParameterizedTypeWrap(ParameterizedType type) {
			super(type);
			final Type[] actualTypeArguments = type.getActualTypeArguments();
			annotatedTypes = new AnnotatedType[actualTypeArguments.length];
			for (int i = 0; i < actualTypeArguments.length; i++) {
				annotatedTypes[i] = wrap(actualTypeArguments[i]);
			}
		}

		@Override
		public AnnotatedType[] getAnnotatedActualTypeArguments() {
			return annotatedTypes;
		}
	}

	private static class AnnotatedArrayTypeWrap extends AnnotatedWrapped implements AnnotatedArrayType {
		private final AnnotatedType component;

		public AnnotatedArrayTypeWrap(Type type) {
			super(type);
			if (type instanceof Class<?> c) {
				component = wrap(c.getComponentType());
			} else if (type instanceof GenericArrayType arr) {
				component = wrap(arr.getGenericComponentType());
			} else {
				throw new RuntimeException("what");
			}
		}

		@Override
		public AnnotatedType getAnnotatedGenericComponentType() {
			return component;
		}
	}

	private static class AnnotatedTypeVariableWrap extends AnnotatedWrapped implements AnnotatedTypeVariable {
		public AnnotatedTypeVariableWrap(TypeVariable<?> type) {
			super(type);
		}

		@Override
		public AnnotatedType[] getAnnotatedBounds() {
			return ((TypeVariable<?>) type).getAnnotatedBounds();
		}
	}


	private static class AnnotatedWrapped implements AnnotatedType {
		protected final Type type;

		public AnnotatedWrapped(Type type) {
			this.type = type;
		}

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

	public static Class<?> getClassFrom(AnnotatedType type) {
		return getClassFrom(type.getType());
	}

	public static Class<?> getClassFrom(Type type) {
		if (type instanceof Class<?> c) return c;
		if (type instanceof ParameterizedType pt) return getClassFrom(pt.getRawType());

		throw new IllegalArgumentException(type.getClass() + ": " + type);
	}
}
