package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.type.Clazz;

import java.lang.reflect.Field;

public record FieldEntry(Clazz clazz, Field field) {

	@Override
	public String toString() {
		return field.getName() + " : " + clazz;
	}
}
