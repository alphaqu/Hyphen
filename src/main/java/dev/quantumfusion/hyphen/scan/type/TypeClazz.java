package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.NotNull;
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
		return defined.getDefinedClass();
	}

	@Override
	public Class<?> getBytecodeClass() {
		return bytecodeBound;
	}

	@Override
	public Clazz define(String typeName) {
		return defined.define(typeName);
	}

	@Override
	public Clazz asSub(Class<?> sub) {
		return defined.asSub(sub);
	}

	@Override
	public List<FieldEntry> getFields() {
		return defined.getFields();
	}

	@Override
	public int defined() {
		return defined.defined();
	}

	@Override
	public String toString() {
		return defined.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		TypeClazz typeClazz = (TypeClazz) o;
		return Objects.equals(bytecodeBound, typeClazz.bytecodeBound);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), bytecodeBound);
	}
}
