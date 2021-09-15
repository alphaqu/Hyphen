package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.util.Color;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class PolymorphicTypeInfo extends TypeInfo{
	public final List<? extends TypeInfo> classInfos;

	public PolymorphicTypeInfo(Class<?> clazz, Map<Class<Annotation>, Object> annotations, List<? extends TypeInfo> classInfos) {
		super(clazz, annotations);
		this.classInfos = classInfos;
	}

	@Override
	public String toFancyString() {
		StringJoiner parameterJoiner = new StringJoiner(
				Color.WHITE + ", ",
				Color.CYAN + "Poly" + Color.PURPLE + "[",
				Color.PURPLE + "]");
		parameterJoiner.setEmptyValue("");
		for (TypeInfo t : this.classInfos) {
			parameterJoiner.add(t.toFancyString());
		}
		return parameterJoiner.toString();
	}

	@Override
	public PolymorphicTypeInfo copy() {
		return new PolymorphicTypeInfo(this.clazz, new HashMap<>(this.annotations), new ArrayList<>(this.classInfos));
	}
}
