package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.annotation.DebugOnly;
import dev.quantumfusion.hyphen.gen.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;

import java.util.Map;

@DebugOnly
public class DebugHandler {

	private final ScanHandler factory;


	public DebugHandler(ScanHandler factory) {
		this.factory = factory;
	}

	public void printMethods(Map<? extends TypeInfo, ? extends SerializerMetadata> methods) {
		StringBuilder sb = new StringBuilder();
		sb.append(Color.WHITE).append(" ->> ").append(Color.RED);
		methods.forEach((typeInfo, serializerMethodMetadata) -> printMethod(sb, serializerMethodMetadata));
		System.out.println(sb);
	}

	private void printMethod(StringBuilder sb, SerializerMetadata serializerMetadata) {
		sb.append(serializerMetadata.toFancyString());
		sb.append('\n');
	}
}
