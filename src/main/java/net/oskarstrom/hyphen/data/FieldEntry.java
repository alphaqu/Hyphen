package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.data.info.TypeInfo;

import java.lang.reflect.Type;

public class FieldEntry {
	public final TypeInfo clazz;
	public final int modifier;
	public final String name;
	public final Type fieldType;


	public FieldEntry(TypeInfo clazz, int modifier, String name, Type fieldType) {
		this.clazz = clazz;
		this.modifier = modifier;
		this.name = name;
		this.fieldType = fieldType;
	}
}
