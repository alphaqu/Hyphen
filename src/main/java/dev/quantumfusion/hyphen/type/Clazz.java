package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class Clazz implements Type {
	protected final Class<?> clazz;

	protected Clazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public static Clazz create(Type type, Clazz parent) {
		Class<?> clazz = (Class<?>) type;
		if (clazz != null && clazz.isArray()) {
			return ArrayClazz.create(clazz, parent);
		}
		return new Clazz(clazz);
	}

	public Class<?> pullClass() {
		return clazz;
	}

	public Class<?> pullBytecodeClass() {
		return clazz;
	}

	public Type getSuper() {
		return pullClass().getGenericSuperclass();
	}

	public Field[] getFields() {
		return pullClass().getDeclaredFields();
	}

	public Clazz getSub(Class<?> clazz) {
		return Clazzifier.create(clazz, this);
	}

	public Clazz getType(String type) {
		return Clazzifier.UNKNOWN;
	}

	@Override
	public String toString() {
		return clazz.getSimpleName();
	}
}
