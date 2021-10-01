package dev.quantumfusion.hyphen.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.util.Color;
import dev.quantumfusion.hyphen.util.TypeUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ParameterizedInfo extends ClassInfo {
	// TODO make this a TypeClassInfo???????????
	public final LinkedHashMap<String, TypeInfo> types;

	public ParameterizedInfo(Class<?> clazz, Type type, AnnotatedType annotatedType, Map<Class<? extends Annotation>, Annotation> annotations, LinkedHashMap<String, TypeInfo> types) {
		super(clazz, type, annotatedType, annotations);
		this.types = types;
	}

	public static TypeInfo createType(ScanHandler handler, TypeInfo source, Class<?> clazz, ParameterizedType type, @Nullable AnnotatedType annotatedType, Map<Class<? extends Annotation>, Annotation> annotations) {
		return new ParameterizedInfo(clazz, type, annotatedType, annotations, TypeUtil.mapTypes(handler, source, type, (AnnotatedParameterizedType) annotatedType));
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
		builder.append(super.getMethodName(absolute));
		builder.append("_E_");
		for (TypeInfo value : types.values()) {
			builder.append(value.getMethodName(absolute));
		}
		builder.append("_3_");
		return builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ParameterizedInfo that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(types, that.types);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), types);
	}

	public ClassInfo copyWithoutTypeKnowledge() {
		LinkedHashMap<String, TypeInfo> typesCloned = new LinkedHashMap<>();
		ClassInfo value = new ClassInfo(Object.class, Map.of());
		types.forEach((s, info) -> typesCloned.put(s, value));
		return new ParameterizedInfo(clazz, type, annotatedType, new HashMap<>(annotations), typesCloned);
	}

}
