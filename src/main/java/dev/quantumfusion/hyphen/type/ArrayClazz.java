package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Direction;
import dev.quantumfusion.hyphen.ScanHandler;

import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.GenericArrayType;

public class ArrayClazz extends Clazz {
	public final Clazz component;

	public ArrayClazz(Class<?> aClass, Clazz component) {
		super(aClass);
		this.component = component;
	}


	public static ArrayClazz create(Class<?> array, Clazz clz, Direction dir) {
		return new ArrayClazz(array, ScanHandler.create(array.getComponentType(), clz, dir));
	}

	public static ArrayClazz createGeneric(AnnotatedArrayType array, Clazz clz, Direction dir) {
		return new ArrayClazz(Object[].class, ScanHandler.create(array.getAnnotatedGenericComponentType(), clz, dir));
	}

	@Override
	public int defined() {
		return 1 + component.defined();
	}

	@Override
	public String toString() {
		return component.toString() + "[]";
	}
}
