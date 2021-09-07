package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.SerializerFactory;

import java.util.Objects;

public class ArrayInfo extends ClassInfo {
	public final ClassInfo values;

	public ArrayInfo(Class<?> clazz, ClassInfo values, SerializerFactory factory) {
		super(clazz, factory);
		this.values = values;
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
}
