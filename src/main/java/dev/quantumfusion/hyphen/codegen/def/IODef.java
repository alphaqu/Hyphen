package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

import static org.objectweb.asm.Opcodes.*;

public class IODef implements SerializerDef {
	final Class<?> primitive;

	public IODef(Class<?> primitive) {
		this.primitive = primitive;
	}

	@Override
	public void writePut(MethodHandler mh, Runnable alloc) {
		mh.varOp(ILOAD, "io");
		alloc.run();
		mh.putIO(this.primitive);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.varOp(ILOAD, "io");
		mh.getIO(this.primitive);
	}

	@Override
	public void writeMeasure(MethodHandler mh, Runnable alloc) {
		Class<?> prim = this.primitive.isArray() ? this.primitive.getComponentType() : this.primitive;
		int size;
		if (prim == boolean.class || prim == byte.class) size = 1;
		else if (prim == short.class || prim == char.class) size = 2;
		else if (prim == int.class || prim == float.class) size = 4;
		else if (prim == long.class || prim == double.class) size = 8;
		else throw new RuntimeException("what");

		if (this.primitive.isArray()) {
			alloc.run();
			mh.op(ARRAYLENGTH);
			mh.visitLdcInsn(size);
			mh.op(IMUL, ICONST_4, IADD);
		} else {
			mh.visitLdcInsn(size);
		}
	}
}
