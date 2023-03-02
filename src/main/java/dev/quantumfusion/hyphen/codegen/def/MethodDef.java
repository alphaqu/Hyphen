package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodInfo;
import dev.quantumfusion.hyphen.codegen.SerializerGenerator;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import org.jetbrains.annotations.Nullable;

import static org.objectweb.asm.Opcodes.ILOAD;

public abstract class MethodDef extends SerializerDef {
	@Nullable
	private MethodInfo getInfo;
	@Nullable
	private MethodInfo putInfo;
	@Nullable
	private MethodInfo measureInfo;
	public final String suffix;

	public MethodDef(Clazz clazz) {
		this(clazz, "");
	}

	public MethodDef(Clazz clazz, String suffix) {
		super(clazz);
		this.suffix = suffix;
	}

	@Override
	public void scan(SerializerGenerator<?, ?> handler) {
		super.scan(handler);
		var definedClass = clazz.getDefinedClass();

		if (!handler.isEnabled(Options.DISABLE_GET)) {
			this.getInfo = handler.createMethodInfo(clazz, "get", suffix, definedClass, handler.ioClass);
		}
		if (!handler.isEnabled(Options.DISABLE_PUT)) {
			this.putInfo = handler.createMethodInfo(clazz, "put", suffix, Void.TYPE, handler.ioClass, definedClass);
		}
		if (!handler.isEnabled(Options.DISABLE_MEASURE) && this.hasDynamicSize()) {
			this.measureInfo = handler.createMethodInfo(clazz, "measure", suffix, long.class, definedClass);
		}

	}

	protected abstract void writeMethodPut(MethodHandler mh, Runnable valueLoad);

	protected abstract void writeMethodGet(MethodHandler mh);

	protected abstract void writeMethodMeasure(MethodHandler mh, Runnable valueLoad);

	@Override
	public void writePut(MethodHandler mh, Runnable valueLoad) {
		mh.loadIO();
		valueLoad.run();
		mh.callInst(putInfo);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.loadIO();
		mh.callInst(getInfo);
	}

	@Override
	public void writeMeasure(MethodHandler mh, Runnable valueLoad) {
		valueLoad.run();
		mh.callInst(measureInfo);
	}

	public void generateMethods(SerializerGenerator<?, ?> handler) {
		if (this.getInfo != null) {
			handler.generateMethod(this.clazz, this.getInfo, false, this::writeMethodGet);
		}

		if (this.putInfo != null) {
			handler.generateMethod(this.clazz, this.putInfo, false, mh -> this.writeMethodPut(mh, () -> mh.parameterOp(ILOAD, 1)));
		}

		if (this.measureInfo != null) {
			handler.generateMethod(this.clazz, this.measureInfo, false, mh -> this.writeMethodMeasure(mh, () -> mh.parameterOp(ILOAD, 0)));
		}
	}

	@Nullable
	public MethodInfo getInfo() {
		return getInfo;
	}

	@Nullable
	public MethodInfo putInfo() {
		return putInfo;
	}

	@Nullable
	public MethodInfo measureInfo() {
		return measureInfo;
	}
}
