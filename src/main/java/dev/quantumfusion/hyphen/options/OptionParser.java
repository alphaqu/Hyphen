package dev.quantumfusion.hyphen.options;

import java.lang.annotation.Annotation;

public interface OptionParser<K> {
	K getDefaultValue();

	K getValueFromAnnotation(Annotation annotation);

	default String getString(K object) {
		return object.toString();
	}

}
