package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;
import dev.quantumfusion.hyphen.util.AnnoUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.Map;

public class Unknown extends Clazz {
	public Unknown() {
		super(null, Map.of(), Map.of());
	}

	public Class<?> pullClass() {
		return Object.class;
	}

	public Class<?> pullBytecodeClass() {
		return Object.class;
	}

	public Clazz getSub(Class<?> clazz) {
		return Clazzifier.create(AnnoUtil.wrap(clazz), this);
	}

	public Clazz defineType(String type) {
		return Clazzifier.UNKNOWN;
	}

	@Override
	public String toString() {
		return "UNKNOWN";
	}

	public static class WrappedAnnotation implements AnnotatedType {
		private static final Annotation[] EMPTY = new Annotation[0];
		private final Type type;

		public WrappedAnnotation(Type type) {
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
			return EMPTY;
		}

		@Override
		public Annotation[] getDeclaredAnnotations() {
			return EMPTY;
		}

	}

}
