package dev.quantumfusion.hyphen.options;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotation.HyphenOptionAnnotation;
import dev.quantumfusion.hyphen.util.Color;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class AnnotationParser {

	@SuppressWarnings("unchecked")
	public static Map<Class<Annotation>, Annotation> parseAnnotations(@Nullable AnnotatedType type) {
		Map<Class<Annotation>, Annotation> annotations = new HashMap<>();
		if (type != null) {
			for (Annotation declaredAnnotation : type.getDeclaredAnnotations()) {
				Class<Annotation> aClass = (Class<Annotation>) declaredAnnotation.annotationType();
				if (aClass.getDeclaredAnnotation(HyphenOptionAnnotation.class) != null) {
					annotations.put(aClass, declaredAnnotation);
				}
			}
		}
		return annotations;
	}


	public static String toFancyString(Map<Class<Annotation>, Annotation> annotations, ScanHandler factory) {
		StringJoiner stringJoiner = new StringJoiner(Color.WHITE + ", ", Color.WHITE + "(", Color.WHITE + ")");

		annotations.forEach((annotationClass, o) -> {
			OptionParser<?> optionParser = factory.hyphenAnnotations.get(annotationClass);
			stringJoiner.add(Color.GREEN + "@" + annotationClass.getSimpleName() + ":" + optionParser.getValueFromAnnotation(o));
		});
		return stringJoiner.toString();
	}


	public static class AnnotationOptionMap<A extends Annotation> extends HashMap<Class<? extends A>, OptionParser<?>> {
	}
}
