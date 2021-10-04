package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class ArrayClazz extends Clazz {
	public Clazz component;

	protected ArrayClazz(Clazz type) {
		super(type.pullClass());
		this.component = type;
	}

	public static ArrayClazz create(Type typeVariable, Clazz parent) {
		Type componentType = null;
		if (typeVariable instanceof GenericArrayType t) componentType = t.getGenericComponentType();
		if (typeVariable instanceof Class<?> c) componentType = c.getComponentType();
		return new ArrayClazz(Clazzifier.create(componentType, parent));
	}

	@Override
	public Clazz defineType(String type) {
		return component.defineType(type);
	}

	@Override
	public String toString() {
		return component.toString() + "[]";
	}

}
