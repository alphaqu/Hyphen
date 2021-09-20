package dev.quantumfusion.hyphen.data.metadata;

import dev.quantumfusion.hyphen.data.info.TypeInfo;

public class ArraySerializerMetadata extends SerializerMetadata {
	public final SerializerMetadata value;

	public ArraySerializerMetadata(TypeInfo clazz, SerializerMetadata value) {
		super(clazz);
		this.value = value;
	}
}
