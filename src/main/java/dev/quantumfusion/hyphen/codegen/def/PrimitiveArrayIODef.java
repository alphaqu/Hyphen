package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodWriter;
import dev.quantumfusion.hyphen.scan.annotations.DataFixedArraySize;
import dev.quantumfusion.hyphen.scan.struct.Struct;

import static org.objectweb.asm.Opcodes.*;

public class PrimitiveArrayIODef extends SerializerDef<Struct> {
	protected final Class<?> primitiveArray;
	protected final Integer fixedSize;

	public PrimitiveArrayIODef(Struct clazz) {
		super(clazz);
		this.primitiveArray = clazz.getValueClass();

		DataFixedArraySize annotation = clazz.getAnnotation(DataFixedArraySize.class);
		this.fixedSize = annotation != null ? annotation.value() : null;
	}

	@Override
	public void writePut(MethodWriter mh, Runnable valueLoad) {
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
	public void writeGet(MethodWriter mh) {
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
	public void writeMeasure(MethodWriter mh, Runnable valueLoad) {
		int size = PrimitiveIODef.getSize(this.primitiveArray.getComponentType());

		valueLoad.run();
		mh.op(ARRAYLENGTH, I2L, ICONST_0 + Integer.numberOfTrailingZeros(size), LSHL);
	}

	@Override
	public long getStaticSize() {
		return fixedSize == null ? 4 : 0;
	}
}
