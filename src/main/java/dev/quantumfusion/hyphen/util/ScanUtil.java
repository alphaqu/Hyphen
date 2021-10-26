package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.scan.annotations.DataGlobalAnnotation;
import dev.quantumfusion.hyphen.scan.annotations.HyphenAnnotation;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
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

	public static Map<Class<? extends Annotation>, Object> acquireAnnotations(SerializerHandler<?, ?> handler, @NotNull AnnotatedType self, @Nullable Clazz parent) {
		var out = new LinkedHashMap<Class<? extends Annotation>, Object>();
		if (parent != null) {
			final Class<?> parentClass = parent.getDefinedClass();
			Package pack = parentClass.getPackage();
			if (pack != null)
				addAnnotations(pack, out);
			addAnnotations(parentClass, out);
		}
		if (self instanceof FieldAnnotatedType fieldAnnotatedType) {
			addAnnotations(fieldAnnotatedType.field, out);
			addAnnotations(fieldAnnotatedType.annotatedType, out);
		} else addAnnotations(self, out);


		if (out.containsKey(DataGlobalAnnotation.class))
			out.putAll(handler.globalAnnotations.get(out.get(DataGlobalAnnotation.class)));

		final Class<?> classFrom = getClassFrom(self);
		if (handler.globalAnnotations.containsKey(classFrom)) {
			out.putAll(handler.globalAnnotations.get(classFrom));
		}

		return out;
	}

	public static void addAnnotations(AnnotatedElement annotations, Map<Class<? extends Annotation>, Object> map) {
		try {
			for (Annotation annotation : annotations.getDeclaredAnnotations()) {
				var annotatedType = annotation.annotationType();
				if (annotatedType.getDeclaredAnnotation(HyphenAnnotation.class) == null) return;
				Object value = null;
				final Method valueGetter = getAnnotationValueGetter(annotation.annotationType());
				if (valueGetter != null)
					value = valueGetter.invoke(annotation);


				map.put(annotatedType, value);
			}
		} catch (InvocationTargetException | IllegalAccessException exception) {
			throw new RuntimeException(exception);
		}
	}

	@Nullable
	public static Method getAnnotationValueGetter(Class<? extends Annotation> annotation) {
		Method methodOut = null;
		for (Method method : annotation.getMethods()) {
			if (method.getDeclaringClass() == Annotation.class) continue;
			if (methodOut != null)
				throw new RuntimeException("Annotation " + annotation.getSimpleName() + " has more than 1 field");
			methodOut = method;
		}

		return methodOut;
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

	public record FieldAnnotatedType(Field field, AnnotatedType annotatedType) implements AnnotatedType {
		@Override
		public Type getType() {
			return annotatedType.getType();
		}

		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
			return field.getAnnotation(annotationClass);
		}

		@Override
		public Annotation[] getAnnotations() {
			return field.getAnnotations();
		}

		@Override
		public Annotation[] getDeclaredAnnotations() {
			return field.getDeclaredAnnotations();
		}

		@Override
		public String toString() {
			return Arrays.toString(field.getDeclaredAnnotations());
		}
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
