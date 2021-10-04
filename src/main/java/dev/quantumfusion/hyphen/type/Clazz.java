package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;
import dev.quantumfusion.hyphen.util.AnnoUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.Map;

public class Clazz {
	public final Map<Class<? extends Annotation>, Annotation> annotations;
	public final Map<Class<? extends Annotation>, Annotation> globalAnnotations;
	private final Class<?> clazz;

	Clazz(Class<?> clazz, Map<Class<? extends Annotation>, Annotation> annotations, Map<Class<? extends Annotation>, Annotation> globalAnnotations) {
		this.clazz = clazz;
		this.annotations = annotations;
		this.globalAnnotations = globalAnnotations;
	}

	public static Clazz create(AnnotatedType type, Clazz parent) {
		Class<?> clazz = (Class<?>) type.getType();
		if (clazz != null && clazz.isArray()) {
			return ArrayClazz.create(type, parent);
		}
		return new Clazz(clazz, AnnoUtil.parseAnnotations(type), Clazzifier.getClassAnnotations(parent));
	}

	public Class<?> pullClass() {
		return clazz;
	}

	public Class<?> pullBytecodeClass() {
		return clazz;
	}

	public Clazz getSub(Class<?> clazz) {
		return Clazzifier.create(AnnoUtil.wrap(clazz), this);
	}

	public Clazz defineType(String type) {
		return Clazzifier.UNKNOWN;
	}

	@Override
	public String toString() {
		return clazz.getSimpleName();
	}

	@Override
	public boolean equals(Object o) {
		return this == o
				|| o instanceof Clazz that
				&& this.annotations.equals(that.annotations)
				&& this.globalAnnotations.equals(that.globalAnnotations)
				&& this.clazz.equals(that.clazz);

	}

	@Override
	public int hashCode() {
		int result = this.annotations.hashCode();
		result = 31 * result + this.globalAnnotations.hashCode();
		result = 31 * result + this.clazz.hashCode();
		return result;
	}
}
