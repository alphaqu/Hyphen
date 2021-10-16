package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodInfo;

public abstract class MethodDef implements SerializerDef {
	protected final MethodInfo getInfo;
	protected final MethodInfo putInfo;
	protected final MethodInfo measureInfo;

	public MethodDef(CodegenHandler<?, ?> handler, String name) {
		this.getInfo = handler.apply(new MethodInfo("get" + name, handler.dataClass, handler.ioClass));
		this.putInfo = handler.apply(new MethodInfo("put" + name, Void.TYPE, handler.dataClass, handler.ioClass));
		this.measureInfo = handler.apply(new MethodInfo("measure" + name, int.class, handler.dataClass));
	}

	abstract void writeMethodGet(MethodHandler mh);

	abstract void writeMethodPut(MethodHandler mh);

	abstract void writeMethodMeasure(MethodHandler mh);

	@Override
	public void writePut(MethodHandler mh) {
		mh.visitMethodInsn(putInfo);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.visitMethodInsn(getInfo);
	}

	@Override
	public void writeMeasure(MethodHandler mh) {
		mh.visitMethodInsn(measureInfo);
	}
}
