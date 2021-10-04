package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.thr.ScanException;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class TypeClazz extends Clazz {
	public Clazz actual;

	protected TypeClazz(TypeVariable<?> typeVariable, Clazz parent) {
		super((Class<?>) typeVariable.getBounds()[0]);
		this.actual = getType(typeVariable, parent);
	}

	public static TypeClazz create(Type typeVariable, Clazz parent) {
		return new TypeClazz((TypeVariable<?>) typeVariable, parent);
	}

	private static Clazz getType(TypeVariable<?> typeVariable, Clazz parent) {
		final Clazz clazz = parent.getType(typeVariable.getTypeName());
		if (clazz == null) {
			throw new ScanException("Type " + typeVariable.getTypeName() + " could not be identified from " + parent);
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
}
