package dev.notalpha.hyphen.scan.struct;

import dev.notalpha.hyphen.scan.StructScanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * A ParameterStruct is a Struct which represents a single type parameter in {@link ClassStruct}.
 * ParameterStructs are pointed from {@link TypeStruct}s that are used in fields or super declarations.
 */
public class ParameterStruct extends Struct {
	public final String typeName;
	public Struct bound;
	public Struct resolved;

	public ParameterStruct(List<Annotation> annotations, Struct bound, Struct resolved, @NotNull String typeName) {
		super(annotations);
		this.bound = bound;
		this.resolved = resolved;
		this.typeName = typeName;
	}

	public ParameterStruct(Struct bound, Struct resolved, String typeName) {
		this(List.of(), bound, resolved, typeName);
	}

	public ParameterStruct(List<Annotation> annotations, Struct resolved, String typeName) {
		this(annotations, ClassStruct.OBJECT, resolved, typeName);
	}

	public ParameterStruct(Struct resolved, String typeName) {
		this(List.of(), resolved, typeName);
	}

	public void extendType(Struct value) {
		if (this.resolved instanceof TypeStruct) {
			this.resolved.extendType(value);
		} else {
			if (this.resolved == ClassStruct.OBJECT) {
				this.resolved = value;
			} else {
				try {
					this.resolved.extendType(value);
				} catch (Throwable throwable) {
					this.resolved = value;
				}
			}
		}
	}

	@Override
	public boolean isInstance(Struct struct) {
		return this.resolved.isInstance(struct);
	}

	@Override
	public @NotNull Class<?> getBytecodeClass() {
		return this.bound.getBytecodeClass();
	}

	@Override
	public @NotNull Class<?> getValueClass() {
		return this.resolved.getValueClass();
	}

	@Override
	public <T extends Annotation> @Nullable T getAnnotation(Class<T> annotationClass) {
		T resolvedAnnotation = resolved.getAnnotation(annotationClass);
		if (resolvedAnnotation != null) {
			return resolvedAnnotation;
		}

		T boundAnnotation = bound.getAnnotation(annotationClass);
		if (boundAnnotation != null) {
			return boundAnnotation;
		}

		return super.getAnnotation(annotationClass);
	}

	@Override
	public <T extends Annotation> @Nullable T[] getAnnotationsByType(Class<T> annotationClass) {
		return StructScanner.mergeArrays(
				resolved.getAnnotationsByType(annotationClass),
				bound.getAnnotationsByType(annotationClass),
				super.getAnnotationsByType(annotationClass)
		);
	}

	@Override
	public Struct getValueStruct() {
		return this.resolved.getValueStruct();
	}

	@Override
	public String toString() {
		String bound = "";
		if (!this.bound.equals(ClassStruct.OBJECT)) {
			bound = "{" + this.bound + "}";
		}

		String resolved = "";

		if (!this.resolved.equals(ClassStruct.OBJECT)) {
			resolved = "=" + this.resolved.getValueStruct();
		}

		return this.typeName + super.toString() + bound + resolved;
	}

	@Override
	public String simpleString() {
		String resolved = "";
		if (!this.resolved.equals(ClassStruct.OBJECT)) {
			resolved = "=" + this.resolved.getValueStruct().simpleString();
		}
		return this.typeName + resolved;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		ParameterStruct that = (ParameterStruct) o;

		if (!Objects.equals(bound, that.bound)) return false;
		if (!Objects.equals(resolved, that.resolved)) return false;
		return Objects.equals(typeName, that.typeName);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (bound != null ? bound.hashCode() : 0);
		result = 31 * result + (resolved != null ? resolved.hashCode() : 0);
		result = 31 * result + (typeName != null ? typeName.hashCode() : 0);
		return result;
	}
}
