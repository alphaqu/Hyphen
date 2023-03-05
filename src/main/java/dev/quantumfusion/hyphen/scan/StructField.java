package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.scan.struct.Struct;

import java.lang.reflect.Field;
import java.util.Objects;

public class StructField {
	public final Field field;
	public final Struct type;

	public StructField(Field field, Struct type) {
		this.field = field;
		this.type = type;
	}

	@Override
	public String toString() {
		return field.getName() + ": " + type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StructField that = (StructField) o;

		if (!Objects.equals(field, that.field)) return false;
		return Objects.equals(type, that.type);
	}

	@Override
	public int hashCode() {
		int result = field != null ? field.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}
}
