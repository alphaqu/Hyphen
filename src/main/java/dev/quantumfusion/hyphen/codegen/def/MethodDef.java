package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodInfo;
import dev.quantumfusion.hyphen.scan.type.Clazz;

public abstract class MethodDef implements SerializerDef {
	public final MethodInfo getInfo;
	public final MethodInfo putInfo;
	public final MethodInfo measureInfo;

	public MethodDef(CodegenHandler<?, ?> handler, Clazz clazz) {
		final Class<?> definedClass = clazz.getDefinedClass();
		final String name = clazz.toString();
		this.getInfo = handler.apply(new MethodInfo("get" + name, definedClass, handler.ioClass));
		this.putInfo = handler.apply(new MethodInfo("put" + name, Void.TYPE, handler.ioClass, definedClass));
		this.measureInfo = handler.apply(new MethodInfo("measure" + name, int.class, definedClass));
	}

	public abstract void writeMethodGet(MethodHandler mh);

	public abstract void writeMethodPut(MethodHandler mh);

	public abstract void writeMethodMeasure(MethodHandler mh);

	@Override
	public void writePut(MethodHandler mh) {
		mh.visitMethodInsn(putInfo);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.visitMethodInsn(getInfo);
	}

	@Override
	public void writeMeasure(MethodHandler mh, Runnable alloc) {
		alloc.run();
		mh.visitMethodInsn(measureInfo);
	}
}
