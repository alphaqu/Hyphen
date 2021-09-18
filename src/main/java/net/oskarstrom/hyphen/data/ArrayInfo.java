package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.SerializerFactory;
import net.oskarstrom.hyphen.util.Color;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArrayInfo extends TypeInfo {
	public final TypeInfo values;

	public ArrayInfo(Class<?> clazz, Map<Class<Annotation>, Object> annotations, TypeInfo values, SerializerFactory factory) {
		super(clazz, annotations, factory);
		this.values = values;
	}


	@Override
	public String toFancyString() {
		return this.clazz.getSimpleName() + Color.YELLOW + "[]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ArrayInfo arrayInfo)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(values, arrayInfo.values);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), values);
	}

	@Override
	public String toString() {
		return values.toString() + "[]";
	}

	@Override
	public ArrayInfo copy() {
		return new ArrayInfo(clazz, new HashMap<>(annotations), values.copy(), this.factory);
	}
}
