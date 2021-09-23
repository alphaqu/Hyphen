package dev.quantumfusion.hyphen.gen.metadata;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.info.TypeInfo;
import org.objectweb.asm.MethodVisitor;

public abstract class SerializerMetadata {
	public final TypeInfo clazz;

	public SerializerMetadata(TypeInfo clazz) {
		this.clazz = clazz;
	}

	public abstract void writeEncode(MethodVisitor mv, TypeInfo parent, Context context);

	public abstract void writeDecode(MethodVisitor mv, TypeInfo parent, Context ctx);


	public String toFancyString() {
		return clazz.toFancyString();
	}

}
