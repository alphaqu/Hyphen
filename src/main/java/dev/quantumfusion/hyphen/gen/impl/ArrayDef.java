package dev.quantumfusion.hyphen.gen.impl;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.gen.FieldEntry;
import dev.quantumfusion.hyphen.gen.VarHandler;
import dev.quantumfusion.hyphen.info.ClassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Map;

import static org.objectweb.asm.Opcodes.*;


public class ArrayDef implements ObjectSerializationDef {
	protected final TypeInfo component;
	protected final ObjectSerializationDef componentDef;

	public ArrayDef(ObjectSerializationDef componentDef, TypeInfo component) {
		this.componentDef = componentDef;
		this.component = component;
	}

	@Override
	public Class<?> getType() {
		return component.clazz;
	}

	@Override
	public void writeEncode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context ctx, Runnable alloc) {
		Label top = new Label();
		Label end = new Label();
		VarHandler var = ctx.var();


		String i = var.createVar("i", int.class);
		String l = var.createVar("l", int.class);
		Class<?> arrayClazz = fieldEntry.clazz().getClazz();
		String array = var.createVar("array", arrayClazz);


		ctx.io().run();
		alloc.run();
		mv.visitInsn(DUP);
		var.IntInsnVar(array, ASTORE);
		mv.visitInsn(ARRAYLENGTH);
		mv.visitInsn(DUP);
		var.IntInsnVar(l, ISTORE);
		ctx.mode().callMethod(mv, "putInt", GenUtil.getVoidMethodDesc(int.class));


		mv.visitInsn(ICONST_0);
		var.IntInsnVar(i, ISTORE);

		mv.visitLabel(top);
		var.IntInsnVar(i, ILOAD);
		var.IntInsnVar(l, ILOAD);
		mv.visitJumpInsn(IF_ICMPGE, end);

		componentDef.writeEncode(mv, parent, new FieldEntry(new ClassInfo(arrayClazz.getComponentType(), Map.of()), 0, null), ctx, () -> {
			var.IntInsnVar(array, ALOAD);
			var.IntInsnVar(i, ILOAD);
			mv.visitInsn(AALOAD);
		});

		mv.visitIincInsn(var.getVar(i), 1);
		mv.visitJumpInsn(GOTO, top);
		mv.visitLabel(end);
	}

	@Override
	public void writeDecode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context ctx) {
		Label top = new Label();
		Label end = new Label();
		VarHandler var = ctx.var();


		String i = var.createVar("i", int.class);
		String l = var.createVar("l", int.class);
		Class<?> arrayClazz = fieldEntry.clazz().getClazz();
		String array = var.createVar("array", arrayClazz);

		ctx.io().run();
		ctx.mode().callMethod(mv, "getInt", GenUtil.getMethodDesc(int.class));
		mv.visitInsn(DUP);
		var.IntInsnVar(l, ISTORE);
		mv.visitTypeInsn(ANEWARRAY, Type.getInternalName(arrayClazz.getComponentType()));
		var.IntInsnVar(array, ASTORE);

		mv.visitInsn(ICONST_0);
		var.IntInsnVar(i, ISTORE);

		mv.visitLabel(top);
		var.IntInsnVar(i, ILOAD);
		var.IntInsnVar(l, ILOAD);
		mv.visitJumpInsn(IF_ICMPGE, end);

		var.IntInsnVar(array, ALOAD);
		var.IntInsnVar(i, ILOAD);
		componentDef.writeDecode(mv, parent, new FieldEntry(new ClassInfo(arrayClazz.getComponentType(), Map.of()), 0, null), ctx);
		mv.visitInsn(AASTORE);


		mv.visitIincInsn(var.getVar(i), 1);
		mv.visitJumpInsn(GOTO, top);
		mv.visitLabel(end);
		var.IntInsnVar(array, ALOAD);
	}

	@Override
	public String toFancyString() {
		return component.toFancyString() + Color.PURPLE + "[]";
	}
}
