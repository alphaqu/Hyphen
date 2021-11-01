package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.scan.Direction;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.util.Map;
import java.util.Objects;

public class ArrayClazz extends Clazz {
	public final Clazz component;

	public ArrayClazz(SerializerHandler<?, ?> handler, @NotNull Class<?> aClass, Map<Class<? extends Annotation>, Object> annotations, Clazz component) {
		super(handler, aClass, annotations);
		this.component = component;
	}

	public static ArrayClazz create(SerializerHandler<?, ?> handler, AnnotatedType array, @Nullable Clazz ctx, Direction dir) {
		final Clazz component;
		if (dir == Direction.SUB) {
			if (ctx instanceof ArrayClazz arrayClazz) {
				// FIXME: this feels incorrect
				// component = Clazzifier.create(handler, getAnnotatedGenericComponentType(array), arrayClazz.component, dir);
			} else throw new IllegalArgumentException(); // FIXME error
		}
		component = Clazzifier.create(handler, getAnnotatedGenericComponentType(array), ctx, dir);
		return new ArrayClazz(handler, component.getBytecodeClass().arrayType(), ScanUtil.acquireAnnotations(handler, array, ctx), component);
	}

	private static AnnotatedType getAnnotatedGenericComponentType(AnnotatedType array) {
		if (array instanceof AnnotatedArrayType annotatedArrayType)
			return annotatedArrayType.getAnnotatedGenericComponentType();
		if (array instanceof ScanUtil.FieldAnnotatedType fieldAnnotatedType)
			return getAnnotatedGenericComponentType(fieldAnnotatedType.annotatedType());
		else return ScanUtil.wrap(ScanUtil.getClassFrom(array).componentType());
	}

	@Override
	public int defined() {
		return 1 + component.defined();
	}

	@Override
	public String toString() {
		return component.toString() + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ArrayClazz that = (ArrayClazz) o;
		return Objects.equals(component, that.component) && Objects.equals(annotations, that.annotations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), component);
	}
}
