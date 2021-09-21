package dev.quantumfusion.hyphen.info;

import dev.quantumfusion.hyphen.util.Color;
import dev.quantumfusion.hyphen.util.TypeUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class ParameterizedClassInfo extends ClassInfo {
	public final LinkedHashMap<String, TypeInfo> types;

	public ParameterizedClassInfo(Class<?> clazz, Map<Class<Annotation>, Annotation> annotations, LinkedHashMap<String, TypeInfo> types) {
		super(clazz, annotations);
		this.types = types;
	}

	public static ParameterizedClassInfo create(Map<Class<Annotation>, Annotation> annotations, TypeInfo source, ParameterizedType type, @Nullable AnnotatedParameterizedType annotatedType) {
		return new ParameterizedClassInfo((Class<?>) type.getRawType(), annotations, TypeUtil.mapTypes(source, type, annotatedType));
	}

	@Override
	public String toString() {
		StringJoiner parameterJoiner = new StringJoiner(", ", "<", ">");
		parameterJoiner.setEmptyValue("");
		for (TypeInfo t : types.values()) {
			parameterJoiner.add(t.toString());
		}
		return super.toString() + parameterJoiner;
	}

	@Override
	public String toFancyString() {
		StringJoiner parameterJoiner = new StringJoiner(
				Color.WHITE + ", ",
				Color.YELLOW + super.toFancyString() + Color.PURPLE + "<",
				Color.PURPLE + ">");
		parameterJoiner.setEmptyValue("");
		for (TypeInfo t : types.values()) {
			parameterJoiner.add(Color.CYAN + t.toFancyString());
		}
		return parameterJoiner.toString();
	}

	@Override
	public String getMethodName(boolean absolute) {
		StringBuilder builder = new StringBuilder();
		builder.append("<");
		for (TypeInfo value : types.values()) {
			builder.append(value.getMethodName(absolute));
		}
		builder.append(">");
		return builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ParameterizedClassInfo that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(types, that.types);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), types);
	}

	public ClassInfo copyWithoutTypeKnowledge() {
		LinkedHashMap<String, TypeInfo> typesCloned = new LinkedHashMap<>();
		ClassInfo value = new ClassInfo(Object.class, null);
		types.forEach((s, info) -> typesCloned.put(s, value));
		return new ParameterizedClassInfo(clazz, new HashMap<>(annotations), typesCloned);
	}

}
