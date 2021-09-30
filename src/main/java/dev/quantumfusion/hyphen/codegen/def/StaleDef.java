package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

import static org.objectweb.asm.Opcodes.*;

public class StaleDef extends SerializerDef {
	@Override
	public Class<?> getType() {
		return null;
	}

	@Override
	public void doPut(MethodHandler mh) {
		mh.visitInsn(POP2);
	}

	@Override
	public void doGet(MethodHandler mh) {
		mh.visitInsn(POP);
		mh.visitInsn(ACONST_NULL);
	}
}
