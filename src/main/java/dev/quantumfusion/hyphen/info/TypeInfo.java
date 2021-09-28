package dev.quantumfusion.hyphen.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.util.ScanUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public abstract class TypeInfo {
	public final Class<?> clazz;
	public final Type type;

	@Nullable
	public final AnnotatedType annotatedType;
	public final Map<Class<? extends Annotation>, Annotation> annotations;
	public final Map<Class<? extends Annotation>, Annotation> classAnnotations;

	public TypeInfo(Class<?> clazz, Type type, @Nullable AnnotatedType annotatedType, @NotNull Map<Class<? extends Annotation>, Annotation> annotations) {
		this.clazz = clazz;
		this.type = type;
		this.annotatedType = annotatedType;
		this.annotations = annotations;
		this.classAnnotations = ScanUtils.parseAnnotations(clazz == null ? null : clazz.getDeclaredAnnotations());
	}

	public abstract MethodMetadata createMetadata(ScanHandler factory);

	public abstract String toFancyString();

	public abstract String getMethodName(boolean absolute);

	@Override
	public boolean equals(Object o) {
		return this == o
				|| o instanceof TypeInfo typeInfo
				&& Objects.equals(this.clazz, typeInfo.clazz)
				&& Objects.equals(this.annotations, typeInfo.annotations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.clazz, this.annotations);
	}

	public Class<?> getClazz() {
		return this.clazz;
	}

}
