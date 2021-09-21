package dev.quantumfusion.hyphen.gen.metadata;

import dev.quantumfusion.hyphen.info.SubclassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;

import java.util.Map;

public class SubclassSerializerMetadata extends SerializerMetadata {
	public final Map<Class<?>, TypeInfo> subtypes;

	public SubclassSerializerMetadata(SubclassInfo clazz, Map<Class<?>, TypeInfo> subtypes) {
		super(clazz);
		this.subtypes = subtypes;
	}


	public String toFancyString() {
		StringBuilder sb = new StringBuilder(super.toFancyString());
		sb.append('\n');
		subtypes.forEach((clazz, info) -> {
			sb.append(Color.YELLOW).append('\t').append(clazz.getSimpleName());
			sb.append(Color.WHITE).append(" ==> ");
			sb.append(Color.BLUE).append(info.toFancyString());
			sb.append('\n');
		});
		return sb.toString();
	}
}
