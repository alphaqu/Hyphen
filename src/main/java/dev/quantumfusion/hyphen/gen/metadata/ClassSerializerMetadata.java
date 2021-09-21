package dev.quantumfusion.hyphen.gen.metadata;

import dev.quantumfusion.hyphen.ObjectSerializationDef;
import dev.quantumfusion.hyphen.info.ClassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassSerializerMetadata extends SerializerMetadata {
	public final Map<FieldEntry, ObjectSerializationDef> fields;

	public ClassSerializerMetadata(ClassInfo clazz) {
		super(clazz);
		this.fields = new LinkedHashMap<>();
	}

	public String toFancyString() {
		StringBuilder sb = new StringBuilder(super.toFancyString());
		sb.append('\n');
		fields.forEach((fieldEntry, objectSerializationDef) -> {
			sb.append(Color.RESET).append('\t').append(fieldEntry.name);
			sb.append(Color.RED).append(" : ");
			sb.append(Color.BLUE).append(objectSerializationDef.toFancyString());
			sb.append('\n');
		});
		return sb.toString();
	}

	public record FieldEntry(TypeInfo clazz, int modifier, String name) {
	}
}
