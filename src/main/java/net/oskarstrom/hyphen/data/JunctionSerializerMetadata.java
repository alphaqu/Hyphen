package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.ObjectSerializationDef;

import java.util.LinkedHashMap;
import java.util.Map;

public class JunctionSerializerMetadata extends SerializerMetadata {
	public final Map<Class<?>, SerializerMetadata> subtypes;

	public JunctionSerializerMetadata(PolymorphicTypeInfo clazz) {
		super(clazz);
		this.subtypes = new LinkedHashMap<>();
	}
}
