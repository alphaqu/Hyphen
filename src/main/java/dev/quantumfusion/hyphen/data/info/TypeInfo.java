package dev.quantumfusion.hyphen.data.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.data.metadata.SerializerMetadata;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

public abstract class TypeInfo {
	public final Class<?> clazz;
	public final Map<Class<Annotation>, Annotation> annotations;

	public TypeInfo(Class<?> clazz, Map<Class<Annotation>, Annotation> annotations) {
		this.clazz = clazz;
		this.annotations = annotations;
	}

	public abstract SerializerMetadata createMetadata(ScanHandler factory);

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
	public Class<?> getRawClass() {
		return this.clazz;
	}

}
