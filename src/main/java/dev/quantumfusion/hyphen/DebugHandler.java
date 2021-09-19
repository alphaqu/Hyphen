package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.annotation.DebugOnly;
import dev.quantumfusion.hyphen.data.info.TypeInfo;
import dev.quantumfusion.hyphen.data.metadata.ClassSerializerMetadata;
import dev.quantumfusion.hyphen.data.metadata.JunctionSerializerMetadata;
import dev.quantumfusion.hyphen.data.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.options.AnnotationParser;
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
		methods.forEach((typeInfo, serializerMethodMetadata) -> {
			sb.append(Color.YELLOW).append(typeInfo.toFancyString()).append(" ").append(AnnotationParser.toFancyString(typeInfo.annotations, factory)).append('\n');
			if (serializerMethodMetadata instanceof ClassSerializerMetadata classSerializerMetadata) {
				this.printClass(sb, classSerializerMetadata);
			} else if (serializerMethodMetadata instanceof JunctionSerializerMetadata junctionSerializerMetadata) {
				// this.printJunction(sb, junctionSerializerMetadata);
			}
		});
		System.out.println(sb);
	}

	private void printJunction(StringBuilder sb, TypeInfo typeInfo, JunctionSerializerMetadata junctionSerializerMetadata) {

	}

	public void printClass(StringBuilder sb, ClassSerializerMetadata serializerMetadata) {
		serializerMetadata.fields.forEach((field, objectSerializationDef) -> {
			if (field == null) {
				sb.append('\t').append(Color.RESET).append("THIS").append(Color.RED).append(" : ").append(objectSerializationDef.toFancyString()).append('\n');
			} else {
				sb.append('\t').append(Color.RESET).append(field.name).append(Color.RED).append(" : ").append(objectSerializationDef.toFancyString()).append('\n');
			}
		});
		sb.append('\n');
	}


}
