package dev.quantumfusion.hyphen.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Annotation Util class.
 */
public final class AnnoUtil {

	/**
	 * Parses annotations to a map which holds the annotation class as key and the instance as the value.
	 *
	 * @param annotatedType The Type to be scanned.
	 * @return The Annotation Map.
	 */
	public static Map<Class<? extends Annotation>, Annotation> parseAnnotations(AnnotatedElement annotatedType) {
		var out = new HashMap<Class<? extends Annotation>, Annotation>();
		for (Annotation declaredAnnotation : annotatedType.getDeclaredAnnotations()) {
			out.put(declaredAnnotation.getClass(), declaredAnnotation);
		}
		return out;
	}

	/**
	 * Nice print method. <br>
	 * Example: {@code @Annotation @AnotherAnnotation}
	 *
	 * @param annotationMap The Annotation Map.
	 * @return The String.
	 */
	public static String inlinedString(Map<Class<? extends Annotation>, ? extends Annotation> annotationMap) {
		StringBuilder builder = new StringBuilder();
		for (Annotation annotation : annotationMap.values()) {
			builder.append('@').append(annotation.annotationType().getSimpleName()).append(' ');
		}
		return builder.toString();
	}

	/**
	 * Creates an AnnotatedType from a regular Type. Used for when you want to erase Annotation knowledge, or you don't have an AnnotatedType.
	 *
	 * @param type The Type to wrap.
	 * @return The wrapped Type.
	 */
	public static AnnotatedType wrap(Type type) {
		return new WrappedAnnotation(type);
	}

	private static class WrappedAnnotation implements AnnotatedType {
		private static final Annotation[] EMPTY = new Annotation[0];
		private final Type type;

		WrappedAnnotation(Type type) {
			this.type = type;
		}

		@Override
		public Type getType() {
			return this.type;
		}

		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
			return null;
		}

		@Override
		public Annotation[] getAnnotations() {
			return EMPTY;
		}

		@Override
		public Annotation[] getDeclaredAnnotations() {
			return EMPTY;
		}

		@Override
		public String toString() {
			return "WrappedAnnotation{" +
					"type=" + this.type +
					'}';
		}
	}
}
