package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.thr.ScanException;
import dev.quantumfusion.hyphen.util.AnnoUtil;
import dev.quantumfusion.hyphen.util.ReflectionUtil;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.TypeVariable;

/**
 * Comes from a ParameterizedClazz. This holds the bounds and the actual.
 */
public class TypeClazz extends Clazz {
	public Clazz actual;

	protected TypeClazz(AnnotatedTypeVariable typeVariable, Clazz parent) {
		super((Class<?>) ((TypeVariable<?>) typeVariable.getType()).getBounds()[0], AnnoUtil.parseAnnotations(typeVariable), ReflectionUtil.getClassAnnotations(parent));
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
}
