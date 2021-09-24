package dev.quantumfusion.hyphen.gen.impl;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.gen.FieldEntry;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.MethodVisitor;


public class IntDef extends AbstractDef {
	@Override
	public Class<?> getType() {
		return int.class;
	}

	@Override
	public void writeEncode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context context, Runnable alloc) {
		context.io().run();
		alloc.run();
		context.mode().callMethod(mv, "putInt", GenUtil.getVoidMethodDesc(int.class));
	}

	@Override
	public void writeDecode(MethodVisitor mv, TypeInfo parent, FieldEntry fieldEntry, Context ctx) {
		ctx.io().run();
		ctx.mode().callMethod(mv, "getInt", GenUtil.getMethodDesc(int.class));
	}
}
