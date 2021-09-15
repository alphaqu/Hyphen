package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.SerializerFactory;

import java.lang.annotation.Annotation;
import java.util.Map;

public class TypeClassInfo extends ClassInfo {
	public final String typeName;
	public final Class<?> type;

	public TypeClassInfo(Class<?> clazz, Map<Class<Annotation>, Object> annotations, SerializerFactory factory, String typeName, Class<?> type) {
		super(clazz, annotations, factory);
		this.typeName = typeName;
		this.type = type;
	}

	@Override
	public Class<?> getRawClass() {
		return type;
	}
}
