package dev.quantumfusion.hyphen.data.metadata;

import dev.quantumfusion.hyphen.ObjectSerializationDef;
import dev.quantumfusion.hyphen.data.FieldEntry;
import dev.quantumfusion.hyphen.data.info.ClassInfo;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassSerializerMetadata extends SerializerMetadata {
	public final Map<FieldEntry, ObjectSerializationDef> fields;

	public ClassSerializerMetadata(ClassInfo clazz) {
		super(clazz);
		this.fields = new LinkedHashMap<>();
	}
}
