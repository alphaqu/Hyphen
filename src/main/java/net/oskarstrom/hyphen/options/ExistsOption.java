package net.oskarstrom.hyphen.options;

import java.lang.annotation.Annotation;

public class ExistsOption implements OptionParser<Boolean> {
	@Override
	public Boolean getDefaultValue() {
		return false;
	}

	@Override
	public Boolean getValueFromAnnotation(Annotation annotation) {
		return true;
	}

}
