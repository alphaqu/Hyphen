package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

import static org.objectweb.asm.Opcodes.*;

public class StaleDef extends SerializerDef {

	public final Class<?> clazz;

	public StaleDef(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public Class<?> getType() {
		return clazz;
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
