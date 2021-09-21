package dev.quantumfusion.hyphen.gen.metadata;

import dev.quantumfusion.hyphen.info.TypeInfo;

public abstract class SerializerMetadata {
	public final TypeInfo clazz;

	public SerializerMetadata(TypeInfo clazz) {
		this.clazz = clazz;
	}

	public String toFancyString() {
		return clazz.toFancyString();
	};
}
