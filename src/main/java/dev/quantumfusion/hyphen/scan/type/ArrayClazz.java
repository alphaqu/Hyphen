package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Clazzifier;

import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;

/**
 * An ArrayClazz is anything that is an array. The component holds what the array's elements are.
 */
public final class ArrayClazz implements Clz {
	private AnnType component;

	private ArrayClazz(AnnType component) {
		this.component = component;
	}

	/**
	 * Creates a new raw array
	 * <p /> Should be cached and be finalized by calling {@link #finish(AnnotatedType, Clazz)}
	 */
	public static ArrayClazz createRawArray() {
		return new ArrayClazz(null);
	}

	public void finish(AnnotatedType annotatedType, Clazz source) {
		if (!(annotatedType instanceof AnnotatedArrayType annotatedArrayType)) throw new IllegalArgumentException();
		this.component = Clazzifier.createAnnotatedType(
				annotatedArrayType.getAnnotatedGenericComponentType(),
				source);
	}

	@Override
	public String toString() {
		return this.component.toString() + "[]";
	}

	@Override
	public boolean equals(Object o) {
		return this == o
				|| o instanceof ArrayClazz that
				&& super.equals(o)
				&& this.component.equals(that.component);

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + this.component.hashCode();
		return result;
	}

	@Override
	public ArrayClazz resolve(Clazz context) {
		AnnType resolved = this.component.resolve(context);
		if (resolved == this.component) // if the component didn't change, we don't need to create a new object
			return this;
		return new ArrayClazz(resolved);
	}
}
