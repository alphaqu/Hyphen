package dev.quantumfusion.hyphen.scan.struct;

import dev.quantumfusion.hyphen.scan.StructScanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * A TypeStruct is a usage of a type parameter in a class.
 */
public class TypeStruct extends Struct {
	public final ParameterStruct parameter;

	public TypeStruct(List<Annotation> annotations, ParameterStruct parameter) {
		super(annotations);
		this.parameter = parameter;
	}

	public TypeStruct(ParameterStruct parameter) {
		this.parameter = parameter;
	}

	public void resolve(Struct value) {
		this.parameter.resolve(value);
	}

	@Override
	public boolean isInstance(Struct struct) {
		return this.parameter.isInstance(struct);
	}

	@Override
	public @NotNull Class<?> getBytecodeClass() {
		return parameter.getBytecodeClass();
	}

	@Override
	public @NotNull Class<?> getValueClass() {
		return parameter.getValueClass();
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return super.isAnnotationPresent(annotationClass) || this.parameter.isAnnotationPresent(annotationClass);
	}

	@Override
	public <T extends Annotation> @Nullable T getAnnotation(Class<T> annotationClass) {
		T annotation = super.getAnnotation(annotationClass);
		if (annotation != null) {
			return annotation;
		}
		return this.parameter.getAnnotation(annotationClass);
	}

	@Override
	public <T extends Annotation> @Nullable T[] getAnnotationsByType(Class<T> annotationClass) {
		return StructScanner.mergeArrays(super.getAnnotationsByType(annotationClass), this.parameter.getAnnotationsByType(annotationClass));
	}

	@Override
	public Struct getValueStruct() {
		return this.parameter.getValueStruct();
	}

	@Override
	public String toString() {
		return "{" + this.parameter + "}" + super.toString();
	}

	@Override
	public String simpleString() {
		return this.parameter.simpleString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		TypeStruct that = (TypeStruct) o;

		return Objects.equals(parameter, that.parameter);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (parameter != null ? parameter.hashCode() : 0);
		return result;
	}
}
