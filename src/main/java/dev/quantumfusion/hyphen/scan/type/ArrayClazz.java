package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.scan.Direction;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;

public class ArrayClazz extends Clazz {
	public final Clazz component;

	public ArrayClazz(@NotNull Class<?> aClass, Annotation[] sourceAnnotations, Annotation[] annotations, Clazz component) {
		super(aClass, sourceAnnotations, annotations);
		this.component = component;
	}

	public static ArrayClazz create(AnnotatedType array, @Nullable Clazz ctx, Direction dir) {
		return new ArrayClazz(Object[].class, array.getAnnotations(), ScanUtil.parseAnnotations(ctx), Clazzifier.create(((AnnotatedArrayType) array).getAnnotatedGenericComponentType(), ctx, dir));
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
