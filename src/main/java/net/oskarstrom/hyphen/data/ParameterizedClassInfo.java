package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.SerializerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.StringJoiner;

public class ParameterizedClassInfo extends ClassInfo implements ParameterizedType {
	public final LinkedHashMap<String, ClassInfo> types;

	public ParameterizedClassInfo(Class<?> clazz, LinkedHashMap<String, ClassInfo> types, SerializerFactory factory) {
		super(clazz, factory);
		this.types = types;
	}


	@Override
	public String toString() {
		StringJoiner parameterJoiner = new StringJoiner(", ", "<", ">");
		parameterJoiner.setEmptyValue("");
		for (ClassInfo t : types.values()) {
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
}
