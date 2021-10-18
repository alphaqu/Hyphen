package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.Variable;
import dev.quantumfusion.hyphen.codegen.statement.ArrayFor;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.util.GenUtil;

import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public abstract class IndexedDef extends MethodDef {
	protected final SerializerDef componentDef;
	protected final Clazz component;
	protected final Consumer<MethodHandler> getterFunc;
	protected final Consumer<MethodHandler> lengthFunc;

	public IndexedDef(SerializerHandler<?, ?> handler, Clazz clazz, Clazz component, Consumer<MethodHandler> getterFunc, Consumer<MethodHandler> lengthFunc) {
		super(handler.codegenHandler, clazz);
		this.component = component;
		this.getterFunc = getterFunc;
		this.lengthFunc = lengthFunc;
		this.componentDef = handler.acquireDef(component);
	}


	public abstract void writeGetConverter(MethodHandler mh);

	@Override
	public void writeMethodGet(MethodHandler mh) {
		final Variable out = mh.addVar("out", Object[].class);
		mh.varOp(ILOAD, "io");
		mh.getIO(int.class);
		mh.typeOp(ANEWARRAY, component.getBytecodeClass());
		mh.op(DUP);
		mh.varOp(ISTORE, out);
		try (var array = ArrayFor.createArray(mh, out, null, null)) {
			mh.op(DUP);
			mh.varOp(ILOAD, "i");
			componentDef.writeGet(mh);
			mh.op(AASTORE);
		}
		writeGetConverter(mh);
	}

	@Override
	public void writeMethodPut(MethodHandler mh, String dataVar) {
		final Variable length = mh.addVar("length", int.class);
		final Variable data = mh.getVar(dataVar);
		mh.varOp(ILOAD, "io", dataVar);
		lengthFunc.accept(mh);
		mh.op(DUP);
		mh.varOp(ISTORE, length);
		mh.putIO(int.class);
		try (var array = ArrayFor.create(mh, data, null, length, () -> getterFunc.accept(mh), () -> lengthFunc.accept(mh))) {
			componentDef.writePut(mh, () -> {
				array.getElement();
				GenUtil.shouldCastGeneric(mh, component);
			});
		}
	}

	@Override
	public void writeMethodMeasure(MethodHandler mh, String dataVar) {
		final Variable data = mh.getVar(dataVar);

		mh.op(ICONST_4);
		try (var array = ArrayFor.create(mh, data, null, null, () -> getterFunc.accept(mh), () -> lengthFunc.accept(mh))) {
			componentDef.writeMeasure(mh, () -> {
				array.getElement();
				GenUtil.shouldCastGeneric(mh, component);
			});
			mh.op(IADD);
		}
	}
}
