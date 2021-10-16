package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.util.GenUtil;

import static org.objectweb.asm.Opcodes.*;

public class IODef implements SerializerDef {
	protected final Class<?> primitive;

	public IODef(Class<?> primitive) {
		this.primitive = primitive;
	}

	@Override
	public void writePut(MethodHandler mh) {
		mh.visitMethodInsn(INVOKEVIRTUAL, mh.ioClass, "put" + getName(), Void.TYPE, primitive);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.visitMethodInsn(INVOKEVIRTUAL, mh.ioClass, "get" + getName(), primitive);
	}

	@Override
	public void writeMeasure(MethodHandler mh, Runnable alloc) {
		Class<?> prim = primitive.isArray() ? primitive.getComponentType() : primitive;
		int size;
		if (prim == boolean.class || prim == byte.class) size = 1;
		else if (prim == short.class || prim == char.class) size = 2;
		else if (prim == int.class || prim == float.class) size = 4;
		else if (prim == long.class || prim == double.class) size = 8;
		else throw new RuntimeException("what");

		if (primitive.isArray()) {
			alloc.run();
			mh.op(ARRAYLENGTH);
			mh.visitLdcInsn(size);
			mh.op(IMUL, ICONST_4, IADD);
		} else {
			mh.visitLdcInsn(size);
		}
	}

	private String getName() {
		if (primitive.isArray())
			return GenUtil.upperCase(primitive.getComponentType().getSimpleName()) + "Array";
		return GenUtil.upperCase(primitive.getSimpleName());
	}
}
