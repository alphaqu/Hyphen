package dev.quantumfusion.hyphen.gen.metadata;

import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;

public class ArraySerializerMetadata extends SerializerMetadata {
	public final SerializerMetadata value;

	public ArraySerializerMetadata(TypeInfo clazz, SerializerMetadata value) {
		super(clazz);
		this.value = value;
	}

	public String toFancyString() {
		return value.clazz.getMethodName(false) + Color.PURPLE + "[]";
	}
}
