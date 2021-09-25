package dev.quantumfusion.hyphen.gen.impl;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.gen.FieldEntry;
import dev.quantumfusion.hyphen.gen.VarHandler;
import dev.quantumfusion.hyphen.info.ArrayInfo;
import dev.quantumfusion.hyphen.info.ClassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Map;

import static org.objectweb.asm.Opcodes.*;


public class ArrayDef implements ObjectSerializationDef {
	protected final ObjectSerializationDef componentDef;
	protected final ArrayInfo arrayInfo;

	public ArrayDef(ObjectSerializationDef componentDef, ArrayInfo arrayInfo) {
		this.componentDef = componentDef;
		this.arrayInfo = arrayInfo;
	}

	@Override
	public Class<?> getType() {
		return this.arrayInfo.values.clazz;
	}

	@Override
	public void writeEncode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context ctx, Runnable alloc) {
		VarHandler var = ctx.var();


		Class<?> arrayClazz = fieldEntry.clazz().getClazz();

		Label top = var.createScope();
		Label end = new Label();


		var i = var.createVar("i", int.class);
		var l = var.createVar("l", int.class);
		var array = var.createVar("array", arrayClazz);


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

		this.componentDef.writeEncode(mv, parent, new FieldEntry(new ClassInfo(arrayClazz.getComponentType(), Map.of()), 0, null), ctx, () -> {
			var.IntInsnVar(array, ALOAD);
			var.IntInsnVar(i, ILOAD);
			mv.visitInsn(AALOAD);
		});

		mv.visitIincInsn(i.index(), 1);
		mv.visitJumpInsn(GOTO, top);
		var.popScope(end);
	}

	@Override
	public void writeDecode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context ctx) {
		VarHandler var = ctx.var();

		Label top = new Label();
		Label end = new Label();

		var.pushScope();

		var i = var.createVar("i", int.class);
		var l = var.createVar("l", int.class);
		Class<?> arrayClazz = fieldEntry.clazz().getClazz();
		var array = var.createVar("array", arrayClazz);

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
		this.componentDef.writeDecode(mv, parent, new FieldEntry(new ClassInfo(arrayClazz.getComponentType(), Map.of()), 0, null), ctx);
		mv.visitInsn(AASTORE);


		mv.visitIincInsn(i.index(), 1);
		mv.visitJumpInsn(GOTO, top);
		mv.visitLabel(end);
		var.IntInsnVar(array, ALOAD);
		var.popScope();
	}

	@Override
	public void writeEncode2(MethodVisitor mv, Context ctx) {
		VarHandler var = ctx.var();


		Class<?> arrayClazz = this.arrayInfo.getClazz();
		String arrayDesc = Type.getDescriptor(arrayClazz);

		var.pushScope();
		var l = var.createVar("l", int.class);
		var array = var.createVar("array", arrayClazz);

		mv.visitInsn(DUP);
		mv.visitIntInsn(ASTORE, array.index());
		mv.visitInsn(ARRAYLENGTH);
		mv.visitInsn(DUP);

		mv.visitIntInsn(ISTORE, l.index());
		ctx.mode().callMethod(mv, "putInt", GenUtil.getVoidMethodDesc(int.class));


		Label top = var.createScope();
		Label end = new Label();

		// by delaying the start of the region where i is defined, we make our output more javac like; helping ff
		var i = var.createVar("i", int.class);

		mv.visitInsn(ICONST_0);
		mv.visitIntInsn(ISTORE, i.index());


		mv.visitLabel(top);
		mv.visitIntInsn(ILOAD, i.index());
		mv.visitIntInsn(ILOAD, l.index());
		mv.visitJumpInsn(IF_ICMPGE, end);

		ctx.var().IntInsnVar("io", ALOAD);
		mv.visitIntInsn(ALOAD, array.index());
		mv.visitIntInsn(ILOAD, i.index());
		mv.visitInsn(AALOAD);
		this.componentDef.writeEncode2(mv, ctx);

		mv.visitIincInsn(i.index(), 1);
		mv.visitJumpInsn(GOTO, top);

		var.popScope(end);
		var.popScope();
	}

	@Override
	public void writeDecode2(MethodVisitor mv, Context ctx) {
		VarHandler var = ctx.var();


		Label top = new Label();
		Label end = new Label();

		var.pushScope();

		var io = var.getVar("io");
		var i = var.createVar("i", int.class);
		var l = var.createVar("l", int.class);
		Class<?> arrayClazz = this.arrayInfo.getClazz();
		var array = var.createVar("array", arrayClazz);

		// io
		ctx.mode().callMethod(mv, "getInt", GenUtil.getMethodDesc(int.class));
		// length
		mv.visitInsn(DUP);
		// length length
		var.IntInsnVar(l, ISTORE);
		// length
		mv.visitTypeInsn(ANEWARRAY, Type.getInternalName(arrayClazz.getComponentType()));
		// array
		var.IntInsnVar(array, ASTORE);
		// --

		mv.visitInsn(ICONST_0);
		var.IntInsnVar(i, ISTORE);
		// i = 0

		mv.visitLabel(top);
		var.IntInsnVar(i, ILOAD);
		var.IntInsnVar(l, ILOAD);
		mv.visitJumpInsn(IF_ICMPGE, end);
		// if(i >= l) break;

		var.IntInsnVar(array, ALOAD);
		var.IntInsnVar(i, ILOAD);
		var.IntInsnVar(io, ALOAD);
		// array i io
		this.componentDef.writeDecode2(mv, ctx);
		// array i element
		mv.visitInsn(AASTORE); // array[i] = element
		// --

		mv.visitIincInsn(i.index(), 1); // i++;
		mv.visitJumpInsn(GOTO, top);
		mv.visitLabel(end);
		// array
		var.IntInsnVar(array, ALOAD);
		var.popScope();
	}

	@Override
	public String toFancyString() {
		return this.arrayInfo.toFancyString();
	}
}
