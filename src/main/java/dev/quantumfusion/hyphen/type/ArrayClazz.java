package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;

import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;

public class ArrayClazz implements Clz {
	private AnnType component;

	public ArrayClazz(AnnType component) {
		this.component = component;
	}

	public static ArrayClazz createArray() {
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
	public ArrayClazz resolve(Clazz source) {
		AnnType resolved = this.component.resolve(source);
		if (resolved == this.component)
			return this;
		return new ArrayClazz(resolved);
	}
}
