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
		mh.varOp(ILOAD, "io", "data");
		mh.op(ARRAYLENGTH);
		mh.op(DUP);
		mh.varOp(ISTORE, length);
		GenUtil.putIO(mh, int.class);
		try (var array = new ArrayFor(mh, "data", "i", length).start()) {
			componentDef.writePut(mh, () -> {
				array.getElement();
				GenUtil.shouldCastGeneric(mh, component);
			});
		}
		mh.op(RETURN);
	}

	@Override
	public void writeMethodGet(MethodHandler mh) {
		mh.addVar("out", Object[].class);
		mh.varOp(ILOAD, "io");
		GenUtil.getIO(mh, int.class);
		mh.visitTypeInsn(ANEWARRAY, component.getBytecodeClass());
		mh.varOp(ISTORE, "out");
		try (var array = new ArrayFor(mh, "out").start()) {
			mh.varOp(ILOAD, "out", "i");
			componentDef.writeGet(mh);
			mh.op(AASTORE);
		}
		mh.varOp(ILOAD, "out");
		mh.op(ARETURN);
	}

	@Override
	public void writeMethodMeasure(MethodHandler mh) {
		mh.addVar("out", int.class);
		mh.op(ICONST_4);
		mh.varOp(ISTORE, "out");
		try (var array = new ArrayFor(mh, "data").start()) {
			mh.varOp(ILOAD, "out");
			componentDef.writeMeasure(mh, () -> {
				array.getElement();
				GenUtil.shouldCastGeneric(mh, component);
			});
			mh.op(IADD);
			mh.varOp(ISTORE, "out");
		}
		mh.varOp(ILOAD, "out");
		mh.op(IRETURN);
	}
}
