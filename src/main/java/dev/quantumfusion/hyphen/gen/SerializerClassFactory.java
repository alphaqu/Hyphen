package dev.quantumfusion.hyphen.gen;

import dev.quantumfusion.hyphen.ObjectSerializationDef;
import dev.quantumfusion.hyphen.data.info.TypeInfo;
import dev.quantumfusion.hyphen.data.metadata.SerializerMetadata;

import java.util.Map;
import java.util.function.Function;

public class SerializerClassFactory {
	private final Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> definitions;
	private final IOMode mode;

	public SerializerClassFactory(Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> definitions, IOMode mode) {
		this.definitions = definitions;
		this.mode = mode;
	}

	public void createMethod(TypeInfo typeInfo, SerializerMetadata serializerMetadata) {

	}

	public Class<?> compile() {
		// the finished serializer
		return null;
	}
}
