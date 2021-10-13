package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Direction;
import dev.quantumfusion.hyphen.ScanHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;

public class ArrayClazz extends Clazz {
	public final Clazz component;

	public ArrayClazz(Class<?> aClass, Clazz component, Annotation[] annotations) {
		super(aClass, annotations);
		this.component = component;
	}

	public static ArrayClazz create(AnnotatedType array, Clazz clz, Direction dir) {
		return new ArrayClazz(Object[].class, ScanHandler.create(((AnnotatedArrayType) array).getAnnotatedGenericComponentType(), clz, dir), array.getAnnotations());
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
