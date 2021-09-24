package dev.quantumfusion.hyphen.gen.impl;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.gen.FieldEntry;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public final class IOArrayDef extends AbstractDef {
	public final String encodeMethod;
	public final String decodeMethod;
	public final Class<?> clazz;


	public IOArrayDef(String encodeMethod, String decodeMethod, Class<?> clazz) {
		this.encodeMethod = encodeMethod;
		this.decodeMethod = decodeMethod;
		this.clazz = clazz;

	}

	public static IOArrayDef create(String name, Class<?> clazz) {
		return new IOArrayDef("put" + name + "Array", "get" + name + "Array", clazz);
	}

	public static IOArrayDef create(Class<?> clazz) {
		final String str = clazz.getComponentType().getSimpleName();
		final String name = str.substring(0, 1).toUpperCase() + str.substring(1);
		return IOArrayDef.create(name, clazz);
	}

	@Override
	public Class<?> getType() {
		return clazz;
	}

	@Override
	public void writeEncode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context ctx, Runnable alloc) {
		ctx.io().run();
		mv.visitInsn(DUP);
		alloc.run();
		mv.visitInsn(DUP_X1);
		mv.visitInsn(ARRAYLENGTH);
		ctx.mode().callMethod(mv, "putInt", GenUtil.getVoidMethodDesc(int.class));
		ctx.mode().callMethod(mv, encodeMethod, GenUtil.getVoidMethodDesc(clazz));
	}

	@Override
	public void writeDecode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context ctx) {
		ctx.io().run();
		mv.visitInsn(DUP);
		ctx.mode().callMethod(mv, "getInt", GenUtil.getMethodDesc(int.class));
		ctx.mode().callMethod(mv, decodeMethod, GenUtil.getMethodDesc(clazz, int.class));
	}
}
