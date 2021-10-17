package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.Variable;
import dev.quantumfusion.hyphen.codegen.statement.ArrayFor;
import dev.quantumfusion.hyphen.scan.type.ArrayClazz;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.util.GenUtil;

import static org.objectweb.asm.Opcodes.*;

public class ArrayDef extends MethodDef {
	protected final SerializerDef componentDef;
	protected final Clazz component;

	public ArrayDef(SerializerHandler<?, ?> handler, ArrayClazz clazz) {
		super(handler.codegenHandler, clazz);
		this.component = clazz.component;
		this.componentDef = handler.acquireDef(component);
	}

	@Override
	public void writeMethodPut(MethodHandler mh) {
		final Variable length = mh.addVar("length", int.class);
		final Variable data = mh.getVar("data");

		mh.varOp(ILOAD, "io", "data");
		mh.op(ARRAYLENGTH);
		mh.op(DUP);
		mh.varOp(ISTORE, length);
		mh.putIO(int.class);
		try (var array = ArrayFor.create(mh, data, null, length)) {
			componentDef.writePut(mh, () -> {
				array.getElement();
				GenUtil.shouldCastGeneric(mh, component);
			});
		}
		mh.op(RETURN);
	}

	@Override
	public void writeMethodGet(MethodHandler mh) {
		final Variable out = mh.addVar("out", Object[].class);
		mh.varOp(ILOAD, "io");
		mh.getIO(int.class);
		mh.typeOp(ANEWARRAY, component.getBytecodeClass());
		mh.varOp(ISTORE, out);
		try (var array = ArrayFor.create(mh, out, null, null)) {
			mh.varOp(ILOAD, "out", "i");
			componentDef.writeGet(mh);
			mh.op(AASTORE);
		}
		mh.varOp(ILOAD, out);
		mh.op(ARETURN);
	}

	@Override
	public void writeMethodMeasure(MethodHandler mh) {
		final Variable out = mh.addVar("out", int.class);
		final Variable data = mh.getVar("data");

		mh.op(ICONST_4);
		mh.varOp(ISTORE, out);
		try (var array = ArrayFor.create(mh, data, null, null)) {
			mh.varOp(ILOAD, out);
			componentDef.writeMeasure(mh, () -> {
				array.getElement();
				GenUtil.shouldCastGeneric(mh, component);
			});
			mh.op(IADD);
			mh.varOp(ISTORE, out);
		}
		mh.varOp(ILOAD, out);
		mh.op(IRETURN);
	}
}
