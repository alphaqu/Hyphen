package dev.quantumfusion.hyphen.data.metadata;

import dev.quantumfusion.hyphen.data.info.PolymorphicTypeInfo;

import java.util.LinkedHashMap;
import java.util.Map;

public class JunctionSerializerMetadata extends SerializerMetadata {
	public final Map<Class<?>, SerializerMetadata> subtypes;

	public JunctionSerializerMetadata(PolymorphicTypeInfo clazz) {
		super(clazz);
		this.subtypes = new LinkedHashMap<>();
	}
}
