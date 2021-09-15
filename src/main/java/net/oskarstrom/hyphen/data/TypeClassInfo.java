package net.oskarstrom.hyphen.data;

import java.lang.annotation.Annotation;
import java.util.Map;

public class TypeClassInfo extends TypeInfo {
	public final String typeName;
	public final Class<?> type;
	public final TypeInfo actual;

	public TypeClassInfo(Class<?> clazz, Map<Class<Annotation>, Object> annotations, String typeName, Class<?> type, TypeInfo actual) {
		super(clazz, annotations);
		this.typeName = typeName;
		this.type = type;
		this.actual = actual;
	}

	@Override
	public String toFancyString() {
		return this.actual.toFancyString();
	}

	@Override
	public TypeInfo copy() {
		return new TypeClassInfo(this.clazz, this.annotations, this.typeName, this.type, this.actual);
	}

	@Override
	public Class<?> getRawClass() {
		return type;
	}
}