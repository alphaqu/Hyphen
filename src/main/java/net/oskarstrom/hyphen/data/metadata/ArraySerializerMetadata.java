package net.oskarstrom.hyphen.data.metadata;

import net.oskarstrom.hyphen.data.info.TypeInfo;

public class ArraySerializerMetadata extends SerializerMetadata{
	public final SerializerMetadata value;

	public ArraySerializerMetadata(TypeInfo clazz, SerializerMetadata value) {
		super(clazz);
		this.value = value;
	}
}
