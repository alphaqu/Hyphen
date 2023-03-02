package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.annotations.DataFixedArraySize;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import static org.objectweb.asm.Opcodes.*;

public class PrimitiveArrayIODef extends SerializerDef {
	protected final Class<?> primitiveArray;
	protected final Integer fixedSize;

	public PrimitiveArrayIODef(Clazz clazz) {
		super(clazz);
		this.primitiveArray = clazz.getDefinedClass();
		this.fixedSize = (Integer) clazz.getAnnotationValue(DataFixedArraySize.class);
	}

	@Override
	public void writePut(MethodHandler mh, Runnable valueLoad) {
		mh.loadIO();
		valueLoad.run();
		if (fixedSize == null) {
			mh.op(DUP, ARRAYLENGTH, DUP);
			mh.loadIO();
			mh.op(SWAP);
			mh.putIO(int.class);
		} else {
			mh.visitLdcInsn(fixedSize);
		}
		mh.putIO(this.primitiveArray);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.loadIO();
		if (fixedSize == null) {
			mh.op(DUP);
			mh.getIO(int.class);
		} else {
			mh.visitLdcInsn(fixedSize);
		}
		mh.getIO(this.primitiveArray);
	}

	@Override
	public void writeMeasure(MethodHandler mh, Runnable valueLoad) {
		int size = PrimitiveIODef.getSize(this.primitiveArray.getComponentType());

		valueLoad.run();
		mh.op(ARRAYLENGTH, I2L, ICONST_0 + Integer.numberOfTrailingZeros(size), LSHL);
	}

	@Override
	public long getStaticSize() {
		return fixedSize == null ? 4 : 0;
	}
}
