package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;
import dev.quantumfusion.hyphen.util.AnnoUtil;
import dev.quantumfusion.hyphen.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.Map;

/**
 * A regular class.
 */
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
		return new Clazz(clazz, AnnoUtil.parseAnnotations(type), ReflectionUtil.getClassAnnotations(parent));
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
}
