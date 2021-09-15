package net.oskarstrom.hyphen.data;

import java.lang.reflect.Type;

public class FieldMetadata {
	public final TypeInfo clazz;
	public final int modifier;
	public final String name;
	public final Type fieldType;


	public FieldMetadata(TypeInfo clazz, int modifier, String name, Type fieldType) {
		this.clazz = clazz;
		this.modifier = modifier;
		this.name = name;
		this.fieldType = fieldType;
	}
}
