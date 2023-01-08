package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.lang.reflect.Field;
import java.util.Map;

public record FieldEntry(Field field, Clazz clazz) {

	public static FieldEntry create(Map.Entry<Field, Clazz> entry) {
		return new FieldEntry(entry.getKey(), entry.getValue());
	}

	public boolean isNullable() {
		return clazz.containsAnnotation(DataNullable.class);
	}

	public String getFieldName() {
		return field.getName();
	}

	public Class<?> getFieldType() {
		return field.getType();
	}

	@Override
	public String toString() {
		return clazz + " " + field.getName();
	}
}
