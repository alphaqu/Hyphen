package net.oskarstrom.hyphen.data.metadata;

import net.oskarstrom.hyphen.data.info.TypeInfo;

public class ArraySerializerMetadata extends SerializerMetadata{
	public final TypeInfo value;

	public ArraySerializerMetadata(TypeInfo clazz, TypeInfo value) {
		super(clazz);
		this.value = value;
	}
}
