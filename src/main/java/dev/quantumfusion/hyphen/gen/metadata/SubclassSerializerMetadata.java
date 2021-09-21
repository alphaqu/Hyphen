package dev.quantumfusion.hyphen.gen.metadata;

import dev.quantumfusion.hyphen.info.SubclassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;

import java.util.Map;

public class SubclassSerializerMetadata extends SerializerMetadata {
	public final Map<Class<?>, TypeInfo> subtypes;

	public SubclassSerializerMetadata(SubclassInfo clazz, Map<Class<?>, TypeInfo> subtypes) {
		super(clazz);
		this.subtypes = subtypes;
	}
}
