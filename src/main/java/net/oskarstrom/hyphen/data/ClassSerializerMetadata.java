package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.ObjectSerializationDef;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClassSerializerMetadata extends SerializerMetadata {
	public final Map<Field, ObjectSerializationDef> fields;

	public ClassSerializerMetadata(ClassInfo clazz) {
		super(clazz);
		this.fields = new LinkedHashMap<>();
	}
}
