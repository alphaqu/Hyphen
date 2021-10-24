package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.lang.reflect.Field;
import java.util.Map;

public record FieldEntry(Field field, Clazz clazz) {

	public static FieldEntry create(Map.Entry<Field, Clazz> entry) {
		return new FieldEntry(entry.getKey(), entry.getValue());
	}

	@Override
	public String toString() {
		return field.getName() + " : " + clazz;
	}
}
