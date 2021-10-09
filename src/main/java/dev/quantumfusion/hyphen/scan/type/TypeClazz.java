package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Map;

/**
 * Comes from a ParameterizedClazz. This holds the bounds and the actual.
 */
public class TypeClazz implements Clz {
	private final String name;
	private final Clz actual;
	private Clz @Nullable[] bounds;
	private final Type[] rawBounds;
	private Clazz context;

	protected TypeClazz(String name, Clz actual, Type[] rawBounds, Clazz context) {
		this.name = name;
		this.actual = actual;
		this.rawBounds = rawBounds;
		this.context = context;
	}


	public static TypeClazz createRaw(TypeVariable<?> typeVariable) {
		return new TypeClazz(typeVariable.getName(), Clazzifier.UNDEFINED, typeVariable.getBounds(), null);
	}

	@Override
	public void finish(AnnotatedType type, Clazz context) {
		if(type != null && !(type instanceof AnnotatedTypeVariable parameterizedType)) throw new IllegalArgumentException("" + type.getClass());

		this.context = context;

		this.bounds = new Clz[this.rawBounds.length];
		for (int i = 0; i < this.rawBounds.length; i++) {
			Type bound = this.rawBounds[i];
			this.bounds[i] = Clazzifier.create(bound, context);
		}
	}

	public String getName() {
		return this.name;
	}

	@Override
	public TypeClazz instantiate(AnnotatedType annotatedType) {
		return this;
	}

	@Override
	public String toString() {
		return "/" + this.actual.toString();
	}

	@Override
	public boolean equals(Object o) {
		return this == o
				|| o instanceof TypeClazz that
				&& this.name.equals(that.name)
				&& this.actual.equals(that.actual)
				&& Arrays.equals(this.bounds, that.bounds)
				&& this.context.clazz.equals(that.context.clazz);

	}

	@Override
	public int hashCode() {
		int result = this.name.hashCode();
		result = 31 * result + this.actual.hashCode();
		result = 31 * result + Arrays.hashCode(this.bounds);
		result = 31 * result + this.context.clazz.hashCode();
		return result;
	}

	public TypeClazz withActual(Clz actual) {
		if (actual.equals(this.actual)) return this;
		return new TypeClazz(
				this.name,
				actual,
				this.rawBounds,
				this.context);
	}

	public TypeClazz resolveFUCKActual(Clazz source) {
		var defined = this.actual.resolve(source);

		// if (defined != this.actual) {
		// a change
		return new TypeClazz(
				this.name,
				defined,
				this.rawBounds,
				this.context);
	}


	@SuppressWarnings({"AssignmentToForLoopParameter", "RedundantSuppression"})
	@Override
	public TypeClazz resolve(Clazz context) {
		if(context.equals(this.context)) return this;
		var defined = context.resolveType(this.name);

		// if (defined != this.actual) {
			// a change
			return new TypeClazz(
					this.name,
					defined,
					this.rawBounds,
					context);
		// }
/*
		for (int i = 0; i < this.bounds.length; i++) {
			Clz bound = this.bounds[i];
			Clz resolvedBound = bound.resolve(source);
			if (bound != resolvedBound) {
				// a change

				Clz[] resolvedBounds = this.bounds.clone();
				resolvedBounds[i] = resolvedBound;

				for (i++; i < this.bounds.length; i++) {
					resolvedBounds[i] = resolvedBounds[i].resolve(source);
				}

				return new TypeClazz(
						this.name,
						this.actual,
						resolvedBounds,
						context);
			}
		}*/

		// return this;
	}

	@Override
	public TypeClazz merge(Clz other, Map<TypeClazz, TypeClazz> types) {
		return null;
	}
}
