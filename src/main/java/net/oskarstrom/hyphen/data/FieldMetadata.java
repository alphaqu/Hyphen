package net.oskarstrom.hyphen.data;

import java.lang.reflect.Type;

public class FieldMetadata {
	public final ClassInfo clazz;
	public final int modifier;
	public final String name;
	public final Type fieldType;


	public FieldMetadata(ClassInfo clazz, int modifier, String name, Type fieldType) {
		this.clazz = clazz;
		this.modifier = modifier;
		this.name = name;
		this.fieldType = fieldType;
	}
}
