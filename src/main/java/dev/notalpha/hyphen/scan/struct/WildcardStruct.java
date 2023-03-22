package dev.notalpha.hyphen.scan.struct;


import dev.notalpha.hyphen.scan.StructScanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A Wildcard Clazz is a Clazz which comes from a wildcard type. It may contain a lower, upper or no bound.
 */
public class WildcardStruct extends Struct  {
	@Nullable
	public final Struct upperBound;
	@Nullable
	public final Struct lowerBound;

	public WildcardStruct(List<Annotation> annotations, @NotNull Struct bound, boolean lowerBound) {
		super(annotations);
		if (lowerBound) {
			this.upperBound = null;
			this.lowerBound = bound;
		} else {
			this.upperBound = bound;
			this.lowerBound = null;
		}
	}

	public WildcardStruct(@NotNull Struct bound, boolean lowerBound) {
		this(List.of(), bound, lowerBound);
	}

	public WildcardStruct(List<Annotation> annotations) {
		this(annotations, ClassStruct.OBJECT, false);
	}

	public WildcardStruct() {
		this(new ArrayList<>());
	}

	public void extendType(Struct value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isInstance(Struct struct) {
		if (this.lowerBound != null) {
			// TODO
			throw new UnsupportedOperationException("TODO");
		}

		return this.upperBound.isInstance(struct);
	}

	@Override
	public @NotNull Class<?> getBytecodeClass() {
		if (this.lowerBound != null) {
			return Object.class;
		}

		return this.upperBound.getBytecodeClass();
	}

	@Override
	public @NotNull Class<?> getValueClass() {
		if (this.lowerBound != null) {
			return Object.class;
		}

		return this.upperBound.getValueClass();
	}

	@Override
	public <T extends Annotation> @Nullable T getAnnotation(Class<T> annotationClass) {
		T boundAnnotation = (this.lowerBound != null ? this.lowerBound : this.upperBound).getAnnotation(annotationClass);
		if (boundAnnotation != null) {
			return boundAnnotation;
		}

		return super.getAnnotation(annotationClass);
	}

	@Override
	public <T extends Annotation> @Nullable T[] getAnnotationsByType(Class<T> annotationClass) {
		T[] boundAnnotation = (this.lowerBound != null ? this.lowerBound : this.upperBound).getAnnotationsByType(annotationClass);
		return StructScanner.mergeArrays(boundAnnotation, super.getAnnotationsByType(annotationClass));
	}

	@Override
	public String toString() {
		String value = "?" + super.toString();
		if (this.lowerBound != null) {
			return value + " super " + this.lowerBound;
		}

		if (this.upperBound instanceof ClassStruct simpleClazz) {
			if (simpleClazz.aClass != Object.class) {
				return value + " extends " + this.upperBound;
			}
		}

		return value;
	}

	@Override
	public String simpleString() {
		String value = "?";
		if (this.lowerBound != null) {
			return value + " super " + this.lowerBound;
		}

		if (this.upperBound instanceof ClassStruct simpleClazz) {
			if (simpleClazz.aClass != Object.class) {
				return value + " extends " + this.upperBound;
			}
		}

		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		WildcardStruct that = (WildcardStruct) o;

		if (!Objects.equals(upperBound, that.upperBound)) return false;
		return Objects.equals(lowerBound, that.lowerBound);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (upperBound != null ? upperBound.hashCode() : 0);
		result = 31 * result + (lowerBound != null ? lowerBound.hashCode() : 0);
		return result;
	}
}
