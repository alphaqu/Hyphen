package net.oskarstrom.hyphen.data.metadata;

import net.oskarstrom.hyphen.ObjectSerializationDef;
import net.oskarstrom.hyphen.data.FieldEntry;
import net.oskarstrom.hyphen.data.info.ClassInfo;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassSerializerMetadata extends SerializerMetadata {
	public final Map<FieldEntry, ObjectSerializationDef> fields;

	public ClassSerializerMetadata(ClassInfo clazz) {
		super(clazz);
		this.fields = new LinkedHashMap<>();
	}
}
