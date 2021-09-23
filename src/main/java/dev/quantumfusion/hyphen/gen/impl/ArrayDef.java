package dev.quantumfusion.hyphen.gen.impl;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.gen.FieldEntry;
import dev.quantumfusion.hyphen.gen.ObjectSerializationDef;
import dev.quantumfusion.hyphen.gen.VarHandler;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;


public class ArrayDef implements ObjectSerializationDef {
	private ObjectSerializationDef componentDef;
	private final TypeInfo component;

	public ArrayDef(ObjectSerializationDef componentDef, TypeInfo component) {
		this.componentDef = componentDef;
		this.component = component;
	}

	@Override
	public Class<?> getType() {
		return component.clazz;
	}

	@Override
	public void writeEncode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context ctx) {
		Label top = new Label();
		Label end = new Label();
		VarHandler var = ctx.var();


		int i = var.createVar("i", int.class);
		int l = var.createOrGetVar("l", int.class);
		int array = var.createVar("array", parent.getClazz());


		Class<?> arrayClazz = fieldEntry.clazz().getClazz();
		String arrayDesc = Type.getDescriptor(arrayClazz);


		var.IntInsnVar("data", ALOAD);
		mv.visitFieldInsn(GETFIELD, Type.getInternalName(parent.getClazz()), fieldEntry.name(), arrayDesc);
		mv.visitIntInsn(ASTORE, array);

		mv.visitIntInsn(ALOAD, array);
		mv.visitFieldInsn(GETFIELD, Type.getInternalName(arrayClazz), "length", "I");
		mv.visitIntInsn(ISTORE, l);

		var.IntInsnVar("io", ALOAD);
		mv.visitIntInsn(ILOAD, l);
		ctx.mode().callMethod(mv, "putInt", GenUtil.getVoidMethodDesc(int.class));


		mv.visitInsn(ICONST_0);
		mv.visitIntInsn(ISTORE, i);

		mv.visitLabel(top);
		mv.visitIntInsn(ILOAD, i);
		mv.visitIntInsn(ILOAD, l);
		mv.visitJumpInsn(IF_ICMPGE, end);

		Class<?> clazz = fieldEntry.clazz().getClazz();
		String internalName = ctx.serializer().getInternalName();
		mv.visitIntInsn(ALOAD, array);
		mv.visitIntInsn(ILOAD, i);
		mv.visitInsn(AALOAD);
		ctx.var().IntInsnVar("io", ALOAD);
		mv.visitMethodInsn(INVOKESTATIC, internalName, fieldEntry.clazz().getMethodName(false) + "_encode", GenUtil.getVoidMethodDesc(clazz, ctx.mode().ioClass), false);


		mv.visitIincInsn(i, 1);
		mv.visitJumpInsn(GOTO, top);
		mv.visitLabel(end);
	}

	@Override
	public void writeDecode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context ctx) {
		Label top = new Label();
		Label end = new Label();
		VarHandler var = ctx.var();


		int i = var.createVar("i", int.class);
		int l = var.createOrGetVar("l", int.class);
		int array = var.createVar("array", parent.getClazz());


		Class<?> clazz1 = component.getClazz();
		System.out.println(clazz1);
		String arrayDesc = Type.getInternalName(clazz1);

		ctx.var().IntInsnVar("io", ALOAD);
		ctx.mode().callMethod(mv, "getInt", GenUtil.getMethodDesc(int.class));
		mv.visitIntInsn(ISTORE, l);
		mv.visitIntInsn(ILOAD, l);
		mv.visitTypeInsn(ANEWARRAY, arrayDesc);
		mv.visitIntInsn(ASTORE, array);

		mv.visitInsn(ICONST_0);
		mv.visitIntInsn(ISTORE, i);

		mv.visitLabel(top);
		mv.visitIntInsn(ILOAD, i);
		mv.visitIntInsn(ILOAD, l);
		mv.visitJumpInsn(IF_ICMPGE, end);

		Class<?> clazz = fieldEntry.clazz().getClazz();
		String internalName = ctx.serializer().getInternalName();


		mv.visitIntInsn(ALOAD, array);
		mv.visitIntInsn(ILOAD, i);
		ctx.var().IntInsnVar("io", ALOAD);
		mv.visitMethodInsn(INVOKESTATIC, internalName, fieldEntry.clazz().getMethodName(false) + "_decode", GenUtil.getMethodDesc(clazz, ctx.mode().ioClass), false);
		mv.visitInsn(AASTORE);


		mv.visitIincInsn(i, 1);
		mv.visitJumpInsn(GOTO, top);
		mv.visitLabel(end);
		mv.visitIntInsn(ALOAD, array);
	}

	@Override
	public String toFancyString() {
		return component.toFancyString() + Color.PURPLE + "[]";
	}
}
