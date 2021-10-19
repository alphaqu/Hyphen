package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TypeClazz extends Clazz {
	private final Clazz defined;
	private final Class<?> bytecodeBound;
	private final String typeName;

	public TypeClazz(Map<Class<? extends Annotation>, Annotation> annotations, Clazz defined, Class<?> bytecodeBound, String typeName) {
		super(bytecodeBound, annotations);
		this.defined = defined;
		this.bytecodeBound = bytecodeBound;
		this.typeName = typeName;
	}

	public static TypeClazz create(AnnotatedType typeVariable, @Nullable Clazz ctx) {
		var type = (TypeVariable<?>) typeVariable.getType();
		var typeName = type.getTypeName();

		if (ctx == null) throw new RuntimeException("Type Knowledge Required");
		return new TypeClazz(ScanUtil.acquireAnnotations(typeVariable, ctx), ctx.define(typeName), Object.class, typeName);
	}

	@Override
	public Class<?> getDefinedClass() {
		return this.defined.getDefinedClass();
	}

	@Override
	public Class<?> getBytecodeClass() {
		return this.bytecodeBound;
	}

	@Override
	public Clazz define(String typeName) {
		return this.defined.define(typeName);
	}

	@Override
	public Clazz asSub(Class<?> sub) {
		return this.defined.asSub(sub);
	}

	@Override
	public List<FieldEntry> getFields() {
		return this.defined.getFields();
	}

	@Override
	public int defined() {
		return this.defined.defined();
	}

	@Override
	public String toString() {
		return this.defined.toString();
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<? extends A> aClass) {
		return this.defined.getAnnotation(aClass);
	}

	@Override
	public boolean containsAnnotation(Class<? extends Annotation> aClass) {
		return this.defined.containsAnnotation(aClass);
	}

	public Clazz getDefined() {
		return this.defined;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		TypeClazz typeClazz = (TypeClazz) o;
		return Objects.equals(this.bytecodeBound, typeClazz.bytecodeBound) && Objects.equals(this.defined, typeClazz.defined);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.bytecodeBound, this.defined);
	}
}
