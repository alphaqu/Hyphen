package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import static org.objectweb.asm.Opcodes.*;

public class EnumDef extends MethodDef {
	private final Class<? extends Enum<?>> en;

	@SuppressWarnings("unchecked")
	public EnumDef(SerializerHandler<?, ?> handler, Clazz clazz) {
		super(handler.codegenHandler, clazz);
		this.en = (Class<? extends Enum<?>>) clazz.getDefinedClass();
	}

	@Override
	public void writeMethodGet(MethodHandler mh) {
		mh.callInst(INVOKESTATIC, this.en, "values", this.en.arrayType());
		mh.varOp(ILOAD, "io");
		mh.getIO(byte.class);
		mh.op(AALOAD);
	}

	@Override
	public void writeMethodPut(MethodHandler mh, Runnable valueLoad) {
		mh.varOp(ILOAD, "io");
		valueLoad.run();
		mh.callInst(INVOKEVIRTUAL, Enum.class, "ordinal", int.class);
		// TODO: dynamically pick size depending on enum entry count
		mh.putIO(byte.class);
	}

	@Override
	public void writeMethodMeasure(MethodHandler mh, Runnable valueLoad) {
		mh.op(ICONST_1);
	}
}
