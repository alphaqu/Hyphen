package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

import static org.objectweb.asm.Opcodes.*;

public class PrimitiveArrayIODef implements SerializerDef {
	protected final Class<?> primitiveArray;

	public PrimitiveArrayIODef(Class<?> primitiveArray) {
		this.primitiveArray = primitiveArray;
	}

	@Override
	public void writePut(MethodHandler mh, Runnable valueLoad) {
		mh.varOp(ILOAD, "io");
		valueLoad.run();
		mh.putIO(this.primitiveArray);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.varOp(ILOAD, "io");
		mh.getIO(this.primitiveArray);
	}

	@Override
	public void writeMeasure(MethodHandler mh, Runnable valueLoad) {
		int size = PrimitiveIODef.getSize(this.primitiveArray.getComponentType());

		valueLoad.run();
		mh.op(ARRAYLENGTH, ICONST_0 + Integer.numberOfTrailingZeros(size), ISHL);
	}

	@Override
	public int getStaticSize() {
		return 4;
	}
}
