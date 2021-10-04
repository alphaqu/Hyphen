package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;
import dev.quantumfusion.hyphen.util.AnnoUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.util.Map;

public class ArrayClazz extends Clazz {
	public Clazz component;

	public ArrayClazz(Map<Class<? extends Annotation>, Annotation> annotations, Map<Class<? extends Annotation>, Annotation> globalAnnotations, Clazz component) {
		super(component.pullClass(), annotations, globalAnnotations);
		this.component = component;
	}

	public static ArrayClazz create(AnnotatedType typeVariable, Clazz parent) {
		AnnotatedType componentType;
		if (typeVariable instanceof AnnotatedArrayType t) componentType = t.getAnnotatedGenericComponentType();
		else componentType = typeVariable;
		return new ArrayClazz(AnnoUtil.parseAnnotations(typeVariable), Clazzifier.getClassAnnotations(parent), Clazzifier.create(componentType, parent));
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
