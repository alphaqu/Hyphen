package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

import java.util.Locale;

import static org.objectweb.asm.Opcodes.I2L;

public class IODef implements SerializerDef {
	private final Class<?> clazz;

	public IODef(Class<?> clazz) {
		this.clazz = clazz;

	}

	@Override
	public Class<?> getType() {
		return clazz;
	}

	private String name() {
		String s = this.clazz.getSimpleName();
		return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
	}

	@Override
	public void doPut(MethodHandler mh) {
		mh.callInstanceMethod(mh.getIOClazz(), "put" + this.name(), null, this.clazz);
	}

	@Override
	public void doGet(MethodHandler mh) {
		mh.callInstanceMethod(mh.getIOClazz(), "get" + this.name(), this.clazz);
	}


	public long getSize() {
		if (this.clazz == boolean.class || this.clazz == byte.class) return 1;
		if (this.clazz == char.class || this.clazz == short.class) return 2;
		if (this.clazz == int.class || this.clazz == float.class) return 4;
		if (this.clazz == long.class || this.clazz == double.class) return 8;
		// String
		return 4;
	}

	@Override
	public boolean needsField() {
		return clazz == String.class;
	}

	@Override
	public void doMeasure(MethodHandler mh) {
		if (clazz.isPrimitive()) {
			mh.visitLdcInsn(getSize());
		} else {
			mh.callInstanceMethod(String.class, "length", int.class);
			// mh.visitInsn(ICONST_2);
			// mh.visitInsn(IMUL);
			mh.visitInsn(I2L);
		}
	}
}
