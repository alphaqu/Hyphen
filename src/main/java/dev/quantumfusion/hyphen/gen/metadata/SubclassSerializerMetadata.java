package dev.quantumfusion.hyphen.gen.metadata;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.info.SubclassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;
import org.objectweb.asm.MethodVisitor;

import java.util.Map;

public class SubclassSerializerMetadata extends SerializerMetadata {
	public final Map<Class<?>, TypeInfo> subtypes;

	public SubclassSerializerMetadata(SubclassInfo clazz, Map<Class<?>, TypeInfo> subtypes) {
		super(clazz);
		this.subtypes = subtypes;
	}


	@Override
	public void writeEncode(MethodVisitor mv, TypeInfo parent, Context context) {

	}

	@Override
	public void writeDecode(MethodVisitor mv, TypeInfo parent, Context ctx) {

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
