package dev.quantumfusion.hyphen.gen;

import dev.quantumfusion.hyphen.ObjectSerializationDef;
import dev.quantumfusion.hyphen.data.info.TypeInfo;
import dev.quantumfusion.hyphen.data.metadata.SerializerMetadata;

import java.util.Map;
import java.util.function.Function;

public class SerializerClassFactory {
	private final Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> definitions;

	public SerializerClassFactory(Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> definitions) {
		this.definitions = definitions;
	}

	public void createMethods(Map<TypeInfo, SerializerMetadata> methods) {

	}
}
