package dev.quantumfusion.hyphen.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotation.SerNull;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.util.ScanUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public abstract class TypeInfo {
	protected final Class<?> clazz;
	protected final Type type;

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

	@Contract(pure = true)
	public abstract String getMethodName(boolean absolute);

	@Nullable
	@Contract(pure = true)
	public Annotation getAnnotation(Class<? extends Annotation> annotation) {
		if (this.annotations.containsKey(annotation)) {
			return this.annotations.get(annotation);
		} else {
			return this.classAnnotations.get(annotation);
		}
	}

	@Contract(pure = true)
	public boolean hasAnnotation(Class<? extends Annotation> annotation) {
		return this.annotations.containsKey(annotation)
				|| this.classAnnotations.containsKey(annotation);
	}

	public boolean isNullable() {
		return this.hasAnnotation(SerNull.class);
	}

	@Override
	public boolean equals(Object o) {
		return this == o || o instanceof TypeInfo typeInfo
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

	public Class<?> getRawType() {
		return this.getClazz();
	}
}
