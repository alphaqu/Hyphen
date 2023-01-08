package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TypeClazz extends Clazz {
	public final Clazz defined;
	public final String typeName;
	private final Class<?> bytecodeBound;

	public TypeClazz(SerializerHandler<?, ?> handler, Map<Class<? extends Annotation>, Object> annotations, Clazz defined, Class<?> bytecodeBound, String typeName) {
		super(handler, bytecodeBound, annotations);
		this.defined = defined;
		this.bytecodeBound = bytecodeBound;
		this.typeName = typeName;
	}

	public static TypeClazz create(SerializerHandler<?, ?> handler, AnnotatedType typeVariable, @Nullable Clazz ctx) {
		var type = (TypeVariable<?>) typeVariable.getType();
		var typeName = type.getTypeName();

		if (ctx == null) {
			throw new RuntimeException("Type Knowledge Required");
		}
		final Type bound = type.getBounds()[0];
		return new TypeClazz(handler, ScanUtil.acquireAnnotations(handler, typeVariable, ctx), ctx.define(typeName), ScanUtil.getClassFrom(bound), typeName);
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
	public String toString() {
		return this.defined.toString() + " = " + typeName + bytecodeBound.getSimpleName();
	}

	public Clazz getDefined() {
		return this.defined;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		TypeClazz typeClazz = (TypeClazz) o;
		return Objects.equals(this.bytecodeBound, typeClazz.bytecodeBound) && Objects.equals(this.defined, typeClazz.defined);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.bytecodeBound, this.defined);
	}
}
