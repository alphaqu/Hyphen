package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;
import dev.quantumfusion.hyphen.thr.ScanException;
import dev.quantumfusion.hyphen.util.AnnoUtil;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.TypeVariable;

public class TypeClazz extends Clazz {
	public Clazz actual;

	protected TypeClazz(AnnotatedTypeVariable typeVariable, Clazz parent) {
		super((Class<?>) ((TypeVariable<?>) typeVariable.getType()).getBounds()[0], AnnoUtil.parseAnnotations(typeVariable), Clazzifier.getClassAnnotations(parent));
		this.actual = getType(typeVariable, parent);
	}

	public static TypeClazz create(AnnotatedType typeVariable, Clazz parent) {
		return new TypeClazz((AnnotatedTypeVariable) typeVariable, parent);
	}

	private static Clazz getType(AnnotatedTypeVariable typeVariable, Clazz parent) {
		final Clazz clazz = parent.defineType(typeVariable.getType().getTypeName());
		if (clazz == null) {
			throw new ScanException("Type " + typeVariable.getType().getTypeName() + " could not be identified from " + parent);
		}
		return clazz;
	}

	@Override
	public Class<?> pullClass() {
		return actual.pullClass();
	}

	@Override
	public String toString() {
		return "/" + actual.toString();
	}

	@Override
	public boolean equals(Object o) {
		return this == o
				|| o instanceof TypeClazz typeClazz
				&& super.equals(o)
				&& this.actual.equals(typeClazz.actual);

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + this.actual.hashCode();
		return result;
	}
}
