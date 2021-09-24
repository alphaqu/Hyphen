package dev.quantumfusion.hyphen.gen.impl;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.gen.FieldEntry;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.MethodVisitor;

public final class IOPrimDef extends AbstractDef {
	public String encodeMethod;
	public String decodeMethod;
	public Class<?> clazz;

	public IOPrimDef(String encodeMethod, String decodeMethod, Class<?> clazz) {
		this.encodeMethod = encodeMethod;
		this.decodeMethod = decodeMethod;
		this.clazz = clazz;
	}

	public static IOPrimDef create(String name, Class<?> clazz) {
		return new IOPrimDef("put" + name, "get" + name, clazz);
	}

	public static IOPrimDef create(Class<?> clazz) {
		final String str = clazz.getSimpleName();
		final String name = str.substring(0, 1).toUpperCase() + str.substring(1);
		return new IOPrimDef("put" + name, "get" + name, clazz);
	}

	@Override
	public Class<?> getType() {
		return clazz;
	}

	@Override
	public void writeEncode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context context, Runnable alloc) {
		context.io().run();
		alloc.run();
		context.mode().callMethod(mv, encodeMethod, GenUtil.getVoidMethodDesc(clazz));
	}

	@Override
	public void writeDecode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context ctx) {
		ctx.io().run();
		ctx.mode().callMethod(mv, decodeMethod, GenUtil.getMethodDesc(clazz));
	}
}
