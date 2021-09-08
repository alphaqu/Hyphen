package net.oskarstrom.hyphen.options;

import java.lang.annotation.Annotation;
import java.util.StringJoiner;
import java.util.function.Function;

public class ArrayOption<O,A extends Annotation> implements OptionParser<O[]> {
	private final Function<A, O[]> getter;

	public ArrayOption(Function<A,O[]> getter) {
		this.getter = getter;
	}

	@Override
	public O[] getDefaultValue() {
		return null;
	}

	@Override
	public O[] getValueFromAnnotation(Annotation annotation) {
		//noinspection unchecked
		return getter.apply((A) annotation);
	}

	@Override
	public String getString(O[] object) {
		StringJoiner stringJoiner = new StringJoiner(", ", "[", "]");
		for (O o : object) {
			stringJoiner.add(o.toString());
		}
		return stringJoiner.toString();
	}
}
