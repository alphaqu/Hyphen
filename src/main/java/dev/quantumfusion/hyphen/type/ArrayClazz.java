package dev.quantumfusion.hyphen.type;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class ArrayClazz extends Clazz {
	protected ArrayClazz(Class<?> clazz) {
		super(clazz);
	}

	@Override
	public Type getSuper() {
		return getComponentType().getGenericSuperclass();
	}

	@Override
	public Field[] getFields() {
		return getComponentType().getDeclaredFields();
	}


	private Class<?> getComponentType() {
		return clazz.getComponentType();
	}

	@Override
	public String toString() {
		return super.toString() + "[]";
	}
}
