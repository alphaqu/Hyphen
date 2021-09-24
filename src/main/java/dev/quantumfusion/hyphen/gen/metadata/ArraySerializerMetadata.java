package dev.quantumfusion.hyphen.gen.metadata;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ArraySerializerMetadata extends SerializerMetadata {
	public final SerializerMetadata value;

	public ArraySerializerMetadata(TypeInfo clazz, SerializerMetadata value) {
		super(clazz);
		this.value = value;
	}

	@Override
	public void writeEncode(MethodVisitor mv, TypeInfo parent, Context context) {

	}

	@Override
	public void writeDecode(MethodVisitor mv, TypeInfo parent, Context ctx) {
		mv.visitInsn(Opcodes.ACONST_NULL);
	}

	public String toFancyString() {
		return value.clazz.getMethodName(false) + Color.PURPLE + "[]";
	}
}
