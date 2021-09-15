package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.SerializerFactory;
import net.oskarstrom.hyphen.util.Color;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ParameterizedClassInfo extends ClassInfo implements ParameterizedType {
	public final LinkedHashMap<String, TypeInfo> types;

	public ParameterizedClassInfo(Class<?> clazz, Map<Class<Annotation>, Object> annotations, SerializerFactory factory, LinkedHashMap<String, TypeInfo> types) {
		super(clazz, annotations, factory);
		this.types = types;
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
	public Type[] getActualTypeArguments() {
		return types.values().toArray(new ClassInfo[0]);
	}

	@Override
	public Type getRawType() {
		return clazz;
	}

	@Override
	public Type getOwnerType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toFancyString() {
		StringJoiner parameterJoiner = new StringJoiner(
				Color.WHITE + ", ",
				super.toFancyString() + Color.PURPLE + "<",
				Color.PURPLE + ">");
		parameterJoiner.setEmptyValue("");
		for (TypeInfo t : types.values()) {
			parameterJoiner.add(t.toFancyString());
		}
		return parameterJoiner.toString();
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


	@Override
	public ClassInfo copy() {
		LinkedHashMap<String, TypeInfo> typesCloned = new LinkedHashMap<>();
		types.forEach((s, info) -> typesCloned.put(s, info.copy()));
		return new ParameterizedClassInfo(clazz, new HashMap<>(annotations), factory, typesCloned);
	}
}
