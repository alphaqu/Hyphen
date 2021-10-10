package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Map;

/**
 * Comes from a ParameterizedClazz. This holds the bounds and the actual.
 */
public class TypeClazz implements Clz {
	private final String name;
	private final Clz actual;
	private Clz @Nullable [] bounds;
	private final Type[] rawBounds;
	private Clazz context;

	protected TypeClazz(String name, Clz actual, Type[] rawBounds, Clazz context) {
		this.name = name;
		this.actual = actual;
		this.rawBounds = rawBounds;
		this.context = context;

		assert !(actual instanceof TypeClazz);
	}


	public static TypeClazz createRaw(TypeVariable<?> typeVariable) {
		return new TypeClazz(typeVariable.getName(), Clazzifier.UNDEFINED, typeVariable.getBounds(), null);
	}

	public void setContext(ParameterizedClazz context) {
		this.context = context;
	}

	@Override
	public void finish(AnnotatedType type, Clazz context) {
		// if(type != null && !(type instanceof AnnotatedTypeVariable parameterizedType)) throw new IllegalArgumentException("" + type.getClass());

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
		return this.context.clazz.getSimpleName() + ":" + this.name + " = " + this.actual.toString();
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
		if (context.equals(this.context)) return this;
		var defined = context.resolveType(this.name);

		if(defined instanceof TypeClazz typeClazz)
			return typeClazz;

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
	public TypeClazz merge(Clz other, Map<TypeClazz, TypeClazz> types, MergeDirection mergeDirection) {
		if(!types.getOrDefault(this, this).equals(this))
			return types.getOrDefault(this, this).merge(other, types, mergeDirection);

		if (other instanceof TypeClazz otherClazz) {
			TypeClazz otherFix = types.getOrDefault(otherClazz, otherClazz);

			if (otherFix.equals(otherClazz)) {

				Clz mergedActual = this.actual.merge(otherFix.actual, types, mergeDirection);

				// todo: this feels like it's missing shit
				return new TypeClazz(this.name, mergedActual, this.rawBounds, null);

			} else {
				TypeClazz merge = this.merge(otherFix, types, mergeDirection);
				types.put(otherClazz, merge);
				return merge;
			}
		} else {
			Clz mergedActual = this.actual.merge(other, types, mergeDirection);

			TypeClazz typeClazz = new TypeClazz(this.name, mergedActual, this.rawBounds, null);
			types.put(this, typeClazz);
			return typeClazz;
		}

	}
}
