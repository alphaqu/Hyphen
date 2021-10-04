package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.type.Unknown;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AnnoUtil {

	public static Map<Class<? extends Annotation>, Annotation> parseAnnotations(AnnotatedElement annotatedType) {
		var out = new HashMap<Class<? extends Annotation>, Annotation>();
		for (Annotation declaredAnnotation : annotatedType.getDeclaredAnnotations()) {
			out.put(declaredAnnotation.getClass(), declaredAnnotation);
		}
		return out;
	}

	public static String inlinedString(Map<Class<? extends Annotation>, Annotation> annotationMap) {
		StringBuilder builder = new StringBuilder();
		for (Annotation annotation : annotationMap.values()) {
			builder.append('@').append(annotation.annotationType().getSimpleName()).append(' ');
		}
		return builder.toString();
	}

	public static AnnotatedType wrap(Type type) {
		return new Unknown.WrappedAnnotation(type);
	}
}
