package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.lang.reflect.Field;

public record FieldEntry(Clazz clazz, Field field) {

	@Override
	public String toString() {
		return field.getName() + " : " + clazz;
	}
}
