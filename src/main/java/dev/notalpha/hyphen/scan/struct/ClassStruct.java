package dev.notalpha.hyphen.scan.struct;


import dev.notalpha.hyphen.scan.StructField;
import dev.notalpha.hyphen.scan.StructParameters;
import dev.notalpha.hyphen.scan.StructScanner;
import dev.notalpha.hyphen.thr.HyphenException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * ClassStruct is a struct with an underlying {@link Class} implementation.
 * This is the most common {@link Struct} implementation, and you can use it to get fields and get super classes.
 * <br><br>
 * The ClassStruct may contain type parameters and the {@link TypeStruct}s coming from ClassStruct have a link to the {@link ParameterStruct} of this ClassStruct.
 * If a {@link TypeStruct} gets defined by {@link Struct#extendType(Struct)} all of the {@link ParameterStruct} Structs coming from the source will also be defined.
 */
public class ClassStruct extends Struct {
	public static final ClassStruct OBJECT = new ClassStruct(List.of(), Object.class, List.of());

	@NotNull
	public final Class<?> aClass;
	private final StructParameters parameters;

	public ClassStruct(List<Annotation> annotations, @NotNull Class<?> aClass, List<ParameterStruct> parameters) {
		super(annotations);
		this.aClass = aClass;
		this.parameters = new StructParameters(parameters);
	}

	public ClassStruct(List<Annotation> annotations, Class<?> aClass) {
		this(annotations, aClass, List.of());
	}

	public ClassStruct(Class<?> aClass) {
		this(List.of(), aClass);
	}

	@NotNull
	public ParameterStruct getParameter(String name) {
		return this.parameters.getParameter(name);
	}

	@Nullable
	private ClassStruct superStruct = null;

	@Nullable
	public ClassStruct getSuper(StructScanner scanner) {
		if (this.superStruct == null) {
			AnnotatedType annotatedSuperclass = aClass.getAnnotatedSuperclass();
			if (annotatedSuperclass == null) {
				this.superStruct = null;
			} else if (annotatedSuperclass.getType() == Object.class) {
				this.superStruct = ClassStruct.OBJECT;
			} else {
				this.superStruct = (ClassStruct) scanner.scan(annotatedSuperclass, this.parameters);
			}
		}

		return this.superStruct;
	}

	@Nullable
	private ClassStruct[] interfaceStructs = null;

	public ClassStruct[] getInterfaces(StructScanner scanner) {
		if (this.interfaceStructs == null) {
			AnnotatedType[] annotatedInterfaces = aClass.getAnnotatedInterfaces();
			this.interfaceStructs = new ClassStruct[annotatedInterfaces.length];
			for (int i = 0; i < annotatedInterfaces.length; i++) {
				AnnotatedType annotatedInterface = annotatedInterfaces[i];
				this.interfaceStructs[i] = (ClassStruct) scanner.scan(annotatedInterface, this.parameters);
			}
		}

		return this.interfaceStructs;
	}

	@Nullable
	private List<StructField> fields = null;

	public List<StructField> getFields(StructScanner scanner) {
		if (this.fields == null) {
			var fields = new ArrayList<StructField>();
			for (Field field : this.aClass.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				Struct type = scanner.scan(field.getAnnotatedType(), this.parameters);
				fields.add(new StructField(field, type));
			}
			this.fields = fields;
		}

		return fields;
	}

	public List<StructField> getAllFields(StructScanner scanner) {
		var fields = new ArrayList<StructField>();
		ClassStruct aSuper = this;
		while (aSuper != null && aSuper != ClassStruct.OBJECT) {
			fields.addAll(0, aSuper.getFields(scanner));
			aSuper = aSuper.getSuper(scanner);
		}
		return fields;
	}

	@Override
	public void extendType(Struct target) {
		if (target instanceof ClassStruct classStruct) {
			try {
				if (classStruct.aClass != this.aClass) {
					// TODO allow for nested inheritance
					throw new IllegalArgumentException("Incompatible classes");
				}

				for (int i = 0; i < this.parameters.list.size(); i++) {
					this.parameters.getParameter(i).extendType(classStruct.parameters.getParameter(i));
				}
			} catch (Throwable throwable) {
				throw new HyphenException("Struct " + target.simpleString() + " is incompatible with " + this.simpleString(), throwable, null);
			}

			return;
		}
		throw new IllegalArgumentException("Incompatible classes, this " + this + " is not compatible with " + target);
	}

	@Override
	public boolean isInstance(Struct struct) {
		if (struct instanceof ClassStruct classStruct) {
			if (!this.aClass.isInstance(classStruct.aClass)) {
				return false;
			}


			for (int i = 0; i < this.parameters.size(); i++) {
				if (!this.parameters.getParameter(i).isInstance(classStruct.parameters.getParameter(i))) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public @NotNull Class<?> getBytecodeClass() {
		return aClass;
	}

	@Override
	public @NotNull Class<?> getValueClass() {
		return aClass;
	}

	@Override
	public String toString() {
		var parameters = "";
		if (!this.parameters.isEmpty()) {
			StringJoiner stringJoiner = new StringJoiner(", ", "<", ">");
			this.parameters.list.forEach((clazz) -> {
				stringJoiner.add(clazz.toString());
			});
			parameters = stringJoiner.toString();
		}

		return this.aClass.getSimpleName() + super.toString() + parameters;
	}

	@Override
	public String simpleString() {
		var parameters = "";
		if (!this.parameters.isEmpty()) {
			StringJoiner stringJoiner = new StringJoiner(", ", "<", ">");
			this.parameters.list.forEach((clazz) -> {
				stringJoiner.add(clazz.simpleString());
			});
			parameters = stringJoiner.toString();
		}

		return this.aClass.getSimpleName() + parameters;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		ClassStruct that = (ClassStruct) o;

		if (!aClass.equals(that.aClass)) return false;
		return Objects.equals(parameters, that.parameters);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + aClass.hashCode();
		result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
		return result;
	}
}
