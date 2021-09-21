package dev.quantumfusion.hyphen.gen.metadata;

import dev.quantumfusion.hyphen.info.TypeInfo;

public class ArraySerializerMetadata extends SerializerMetadata {
	public final SerializerMetadata value;

	public ArraySerializerMetadata(TypeInfo clazz, SerializerMetadata value) {
		super(clazz);
		this.value = value;
	}
}
