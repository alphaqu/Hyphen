package dev.quantumfusion.hyphen.gen.impl;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.gen.FieldEntry;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETFIELD;


public class IntDef extends AbstractDef {
	@Override
	public Class<?> getType() {
		return int.class;
	}

	@Override
	public void writeEncode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context context) {
		context.var().IntInsnVar("io", ALOAD);
		context.var().IntInsnVar("data", ALOAD);

		mv.visitFieldInsn(GETFIELD, Type.getInternalName(parent.getClazz()), fieldEntry.name(), Type.getDescriptor(int.class));
		context.mode().callMethod(mv, "putInt", GenUtil.getVoidMethodDesc(int.class));
	}

	@Override
	public void writeDecode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context ctx) {
		ctx.var().IntInsnVar("io", ALOAD);
		ctx.mode().callMethod(mv, "getInt", GenUtil.getMethodDesc(int.class));
	}
}
