package dev.notalpha.hyphen.scan.struct;

import dev.notalpha.hyphen.scan.StructScanner;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link Struct} is a generic type for use in the Hyphen Serializer.
 * The only thing every type has in common is a list of annotations although the list may be empty.
 * <br>
 * <br>
 * The most common struct implementation is {@link ClassStruct}
 */
public abstract class Struct {
	private final Map<Class<? extends Annotation>, Annotation> annotations;
	private final List<Annotation> annotationsList;

	public Struct(List<Annotation> annotations) {
		this.annotationsList = List.copyOf(annotations);
		if (annotations.isEmpty()) {
			this.annotations = Map.of();
		} else  {
			this.annotations = new HashMap<>();
			for (Annotation annotation : annotations) {
				this.annotations.put(annotation.annotationType(), annotation);
			}
		}
	}

	public Struct() {
		this(List.of());
	}

	/**
	 * Tries to resolve inner types like parameters by overlaying the value parameter.
	 * <br><br>
	 * This is used for things like type tracing when coming from a defined super class but an unknown subtype.
	 * You can step by step do {@link ClassStruct#getSuper(StructScanner)} and then when you hit the same class as the defined super.
	 * You call {@link ClassStruct#extendType(Struct)} and the Unknown types will get defined from the defined super.
	 * And because all the parameters are linked, your original subtype will now have defined types.
	 *
	 * @param value
	 */
	public abstract void extendType(Struct value);

	/**
	 * Checks if the current struct is compatible with other structs.
	 * @param struct The struct to check if we are compatible with them.
	 */
	public abstract boolean isInstance(Struct struct);

	/**
	 * Gets the class that is present in the bytecode.
	 * This is different from {@link Struct#getValueClass} as this does not change depending on the resolved type that is traced through parameters.
	 *
	 * @return Class present in bytecode
	 * @see Struct#getValueClass
	 */
	@NotNull
	@Contract(pure = true)
	public abstract Class<?> getBytecodeClass();

	/**
	 * Gets the class which a value is guaranteed to be.
	 * <br>
	 * This is different from {@link Struct#getBytecodeClass}
	 * because this is the class which contains all the fields and properties which may be different from what the original sourced type is.
	 * <br>
	 * <br>
	 * An easy example of this would be a {@code List<Integer>}.
	 * The inner holding array bytecode class is always {@code Object[]} because of java type erasure. But the intended type is {@code Integer[]}.
	 *
	 * @return Class intended to be processed
	 * @see Struct#getBytecodeClass
	 */
	@NotNull
	@Contract(pure = true)
	public abstract Class<?> getValueClass();

	/**
	 * Returns true if the annotation class is present anywhere in the struct.
	 * @param annotationClass The class corresponding to the annotation.
	 * @return If the annotation is present.
	 */
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return this.getAnnotation(annotationClass) != null;
	}

	/**
	 * Returns the first annotation that is present in the struct. Else null.
	 * @param annotationClass The class corresponding to the annotation.
	 * @return The annotation which matches the class
	 */
	@Nullable
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		//noinspection unchecked
		return (T) this.annotations.get(annotationClass);
	}

	/**
	 * Gets all the annotations in the structure.
	 * @param annotationClass The class corresponding to the annotation.
	 * @return A list of annotations with the given class
	 */
	public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
		//noinspection unchecked
		return (T[]) new Annotation[] {
				this.annotations.get(annotationClass)
		};
	}

	public List<Annotation> getAnnotations() {
		return this.annotationsList;
	}

	/**
	 * Gets the pure representation of the structure, this removes things like type parameter wrappers.
	 * @return ArrayStruct, ClassStruct, WildcardStruct or UnknownStruct
	 */
	public Struct getValueStruct() {
		return this;
	}

	@Override
	public String toString() {
		return StructScanner.writeAnnotations(this.annotations);
	}

	public abstract String simpleString();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Struct clazz = (Struct) o;

		return Objects.equals(annotations, clazz.annotations);
	}

	@Override
	public int hashCode() {
		return annotations != null ? annotations.hashCode() : 0;
	}
}
