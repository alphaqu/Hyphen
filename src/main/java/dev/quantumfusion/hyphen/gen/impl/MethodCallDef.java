package dev.quantumfusion.hyphen.gen.impl;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.gen.FieldEntry;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

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
	public void writeEncode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context context, Runnable alloc) {
		Class<?> clazz = fieldEntry.clazz().getClazz();
		String internalName = context.serializer().getInternalName();
		alloc.run();
		context.io().run();
		mv.visitMethodInsn(INVOKESTATIC, internalName, fieldEntry.clazz().getMethodName(false) + "_encode", GenUtil.getVoidMethodDesc(clazz, context.mode().ioClass), false);
	}

	@Override
	public void writeDecode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context ctx) {
		Class<?> clazz = fieldEntry.clazz().getClazz();
		String internalName = ctx.serializer().getInternalName();
		ctx.io().run();
		mv.visitMethodInsn(INVOKESTATIC, internalName, fieldEntry.clazz().getMethodName(false) + "_decode", GenUtil.getMethodDesc(clazz, ctx.mode().ioClass), false);
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
