package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

public class PrimitiveIODef implements SerializerDef {
	protected final Class<?> primitive;
	protected final int size;

	public PrimitiveIODef(Class<?> primitive) {
		this.primitive = primitive;
		size = getSize(primitive);
	}

	public static int getSize(Class<?> primitive) {
		if (primitive == boolean.class || primitive == byte.class) {
			return 1;
		} else if (primitive == short.class || primitive == char.class) {
			return 2;
		} else if (primitive == int.class || primitive == float.class) {
			return 4;
		} else if (primitive == long.class || primitive == double.class) {
			return 8;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void writePut(MethodHandler mh, Runnable valueLoad) {
		mh.loadIO();
		valueLoad.run();
		mh.putIO(this.primitive);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.loadIO();
		mh.getIO(this.primitive);
	}

	@Override
	public int getStaticSize() {
		return this.size;
	}

	@Override
	public boolean hasDynamicSize() {
		return false;
	}
}
