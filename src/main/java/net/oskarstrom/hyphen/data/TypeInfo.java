package net.oskarstrom.hyphen.data;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

public abstract class TypeInfo {
	public final Class<?> clazz;
	public final Map<Class<Annotation>, Object> annotations;

	TypeInfo(Class<?> clazz, Map<Class<Annotation>, Object> annotations) {
		this.clazz = clazz;
		this.annotations = annotations;
	}

	public abstract String toFancyString();

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

	public abstract TypeInfo copy();

	public Class<?> getRawClass(){
		return this.clazz;
	}
}
