package net.oskarstrom.hyphen.options;

import java.lang.annotation.Annotation;

public class SimpleAnnotationOption<K extends Annotation> implements OptionParser<K>{
	@Override
	public K getDefaultValue() {
		return null;
	}

	@Override
	public K getValueFromAnnotation(Annotation annotation) {
		return (K)annotation;
	}
}
