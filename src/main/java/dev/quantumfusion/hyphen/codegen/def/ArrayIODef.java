package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

import java.util.Locale;

import static org.objectweb.asm.Opcodes.*;

public class ArrayIODef implements SerializerDef {
	private final Class<?> clazz;

	public ArrayIODef(Class<?> clazz) {
		this.clazz = clazz;

	}

	@Override
	public Class<?> getType() {
		return clazz;
	}

	private String name() {
		String s = this.clazz.componentType().getSimpleName() + "Array";
		return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
	}

	@Override
	public void doPut(MethodHandler mh) {
		mh.visitInsn(DUP2);
		mh.visitInsn(ARRAYLENGTH);
		mh.callIOPut(int.class);
		mh.callInstanceMethod(mh.getIOClazz(), "put" + this.name(), null, this.clazz);
	}

	@Override
	public void doGet(MethodHandler mh) {
		mh.visitInsn(DUP);
		mh.callIOGet(int.class);
		mh.callInstanceMethod(mh.getIOClazz(), "get" + this.name(), this.clazz, int.class);
	}
}
