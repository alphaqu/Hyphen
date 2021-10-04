package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class TypeArrayClazz extends Clazz {
	public Clazz component;

	protected TypeArrayClazz(Clazz type, Clazz parent) {
		super(type.pullClass());
		this.component = type;
	}

	public static TypeArrayClazz create(Type typeVariable, Clazz parent) {
		final Type componentType = ((GenericArrayType) typeVariable).getGenericComponentType();
		return new TypeArrayClazz(Clazzifier.create(componentType, parent), parent);
	}

	@Override
	public Clazz getType(String type) {
		return component.getType(type);
	}

	@Override
	public String toString() {
		return component.toString() + "[]";
	}

}
