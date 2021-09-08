package net.oskarstrom.hyphen.options;

import net.oskarstrom.hyphen.util.Color;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class AnnotationParser {

	public static <A extends Annotation> Map<Class<A>, Object> parseAnnotations(AnnotatedType type, Map<Class<? extends Annotation>, OptionParser<?>> annotations) {
		Map<Class<A>, Object> annotationsOut = new HashMap<>();
		annotations.forEach((annotation, option) -> {
			//noinspection unchecked
			Class<A> boundAnnotation = (Class<A>) annotation;
			if (type != null) {
				Annotation apply = type.getDeclaredAnnotation(boundAnnotation);
				if (apply != null) {
					annotationsOut.put(boundAnnotation, option.getValueFromAnnotation(apply));
				} else {
					Object defaultValue = option.getDefaultValue();
					if (defaultValue != null) {
						annotationsOut.put(boundAnnotation, defaultValue);
					}
				}
			} else {
				Object defaultValue = option.getDefaultValue();
				if (defaultValue != null) {
					annotationsOut.put(boundAnnotation, defaultValue);
				}
			}
		});


		return annotationsOut;
	}


	public static String toFancyString(Map<Class<Annotation>, Object> annotations) {
		StringJoiner stringJoiner = new StringJoiner(Color.WHITE + ", ", Color.WHITE + "(", Color.WHITE + ")");
		annotations.forEach((annotationClass, o) -> stringJoiner.add(Color.GREEN + "@" + annotationClass.getSimpleName() + ":" + o.toString()));
		return stringJoiner.toString();
	}


	public static class AnnotationOptionMap<A extends Annotation> extends HashMap<Class<? extends A>, OptionParser<?>> {
	}
}
