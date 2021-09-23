package dev.quantumfusion.hyphen.gen.impl;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.gen.FieldEntry;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class MethodCallDef extends AbstractDef {
	public final TypeInfo target;

	public MethodCallDef(TypeInfo target) {
		this.target = target;
	}

	@Override
	public Class<?> getType() {
		return Object.class;
	}

	@Override
	public void writeEncode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context context) {
		Class<?> clazz = fieldEntry.clazz().getClazz();
		String internalName = context.serializer().getInternalName();
		context.var().IntInsnVar("data", ALOAD);
		mv.visitFieldInsn(GETFIELD, Type.getInternalName(parent.getClazz()), fieldEntry.name(), Type.getDescriptor(clazz));
		context.var().IntInsnVar("io", ALOAD);
		mv.visitMethodInsn(INVOKESTATIC, internalName, fieldEntry.clazz().getMethodName(false) + "_encode", GenUtil.getVoidMethodDesc(clazz, context.mode().ioClass), false);
	}

	@Override
	public void writeDecode(MethodVisitor methodVisitor, TypeInfo parent, FieldEntry fieldEntry, Context ctx) {
	}

	@Override
	public String toString() {
		return "MethodCallDef{" + this.target.toString() + '}';
	}

	@Override
	public String toFancyString() {
		return Color.BLUE + "MethodCallDef" + Color.WHITE + " ==> " + this.target.toFancyString();
	}
}
