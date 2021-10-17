package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodInfo;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import static org.objectweb.asm.Opcodes.ILOAD;

public abstract class MethodDef implements SerializerDef {
	public final MethodInfo getInfo;
	public final MethodInfo putInfo;
	public final MethodInfo measureInfo;
	protected final Clazz clazz;

	public MethodDef(CodegenHandler<?, ?> handler, Clazz clazz){
		this(handler, clazz, clazz.toString());
	}

	public MethodDef(CodegenHandler<?, ?> handler, Clazz clazz, String name) {
		this.clazz = clazz;
		final Class<?> definedClass = clazz.getDefinedClass();
		this.getInfo = handler.apply(new MethodInfo("get" + name, definedClass, handler.ioClass));
		this.putInfo = handler.apply(new MethodInfo("put" + name, Void.TYPE, handler.ioClass, definedClass));
		this.measureInfo = handler.apply(new MethodInfo("measure" + name, int.class, definedClass));
	}

	public abstract void writeMethodGet(MethodHandler mh);

	public abstract void writeMethodPut(MethodHandler mh);

	public abstract void writeMethodMeasure(MethodHandler mh);

	@Override
	public void writePut(MethodHandler mh, Runnable alloc) {
		mh.varOp(ILOAD, "io");
		alloc.run();
		mh.visitMethodInsn(putInfo);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.varOp(ILOAD, "io");
		mh.visitMethodInsn(getInfo);
	}

	@Override
	public void writeMeasure(MethodHandler mh, Runnable alloc) {
		alloc.run();
		mh.visitMethodInsn(measureInfo);
	}
}
