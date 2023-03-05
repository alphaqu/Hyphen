package dev.quantumfusion.hyphen.scan.struct;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

public class ArrayStruct extends Struct {
	public final Struct component;

	public ArrayStruct(List<Annotation> annotations, Struct component) {
		super(annotations);
		this.component = component;
	}

	public ArrayStruct(Struct component) {
		this(List.of(), component);
	}

	@Override
	public @NotNull Class<?> getBytecodeClass() {
		return component.getBytecodeClass().arrayType();
	}

	@Override
	public @NotNull Class<?> getValueClass() {
		return component.getValueClass().arrayType();
	}

	@Override
	public void resolve(Struct value) {
		if (value instanceof ArrayStruct arrayStruct) {
			this.component.resolve(arrayStruct.component);
		}

		throw new IllegalArgumentException();
	}

	@Override
	public boolean isInstance(Struct struct) {
		if (struct instanceof ArrayStruct arrayStruct) {
			return this.component.isInstance(arrayStruct.component);
		}

		return false;
	}

	@Override
	public String toString() {
		return component + "[" + super.toString() + "]";
	}

	@Override
	public String simpleString() {
		return component.simpleString() + "[]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		ArrayStruct that = (ArrayStruct) o;

		return Objects.equals(component, that.component);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (component != null ? component.hashCode() : 0);
		return result;
	}
}
