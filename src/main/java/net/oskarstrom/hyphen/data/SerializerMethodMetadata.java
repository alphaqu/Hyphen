package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.ObjectSerializationDef;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class SerializerMethodMetadata {
	public final ClassInfo clazz;
	public final Map<Field, ObjectSerializationDef> fields;

	public SerializerMethodMetadata(ClassInfo clazz) {
		this.clazz = clazz;
		this.fields = new LinkedHashMap<>();
	}
}
