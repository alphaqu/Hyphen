package dev.notalpha.hyphen.codegen.def;

import dev.notalpha.hyphen.Options;
import dev.notalpha.hyphen.SerializerGenerator;
import dev.notalpha.hyphen.codegen.MethodInfo;
import dev.notalpha.hyphen.codegen.MethodWriter;
import dev.notalpha.hyphen.scan.struct.Struct;
import org.jetbrains.annotations.Nullable;

import static org.objectweb.asm.Opcodes.ILOAD;

public abstract class MethodDef<D extends Struct> extends SerializerDef<D> {
	@Nullable
	private MethodInfo getInfo;
	@Nullable
	private MethodInfo putInfo;
	@Nullable
	private MethodInfo measureInfo;
	public final String suffix;

	public MethodDef(D struct) {
		this(struct, "");
	}

	public MethodDef(D struct, String suffix) {
		super(struct);
		this.suffix = suffix;
	}

	@Override
	public void scan(SerializerGenerator<?, ?> handler) {
		super.scan(handler);
		var definedClass = struct.getValueClass();

		if (!handler.isEnabled(Options.DISABLE_GET)) {
			this.getInfo = handler.createMethodInfo(struct, "get", suffix, definedClass, handler.ioClass);
		}
		if (!handler.isEnabled(Options.DISABLE_PUT)) {
			this.putInfo = handler.createMethodInfo(struct, "put", suffix, Void.TYPE, handler.ioClass, definedClass);
		}
		if (!handler.isEnabled(Options.DISABLE_MEASURE) && this.hasDynamicSize()) {
			this.measureInfo = handler.createMethodInfo(struct, "measure", suffix, long.class, definedClass);
		}

	}

	protected abstract void writeMethodPut(MethodWriter mh, Runnable valueLoad);

	protected abstract void writeMethodGet(MethodWriter mh);

	protected abstract void writeMethodMeasure(MethodWriter mh, Runnable valueLoad);

	@Override
	public void writePut(MethodWriter mh, Runnable valueLoad) {
		mh.loadIO();
		valueLoad.run();
		mh.callInst(putInfo);
	}

	@Override
	public void writeGet(MethodWriter mh) {
		mh.loadIO();
		mh.callInst(getInfo);
	}

	@Override
	public void writeMeasure(MethodWriter mh, Runnable valueLoad) {
		valueLoad.run();
		mh.callInst(measureInfo);
	}

	public void generateMethods(SerializerGenerator<?, ?> handler) {
		if (this.getInfo != null) {
			handler.generateMethod(this.struct, this.getInfo, false, this::writeMethodGet);
		}

		if (this.putInfo != null) {
			handler.generateMethod(this.struct, this.putInfo, false, mh -> this.writeMethodPut(mh, () -> mh.parameterOp(ILOAD, 1)));
		}

		if (this.measureInfo != null) {
			handler.generateMethod(this.struct, this.measureInfo, false, mh -> this.writeMethodMeasure(mh, () -> mh.parameterOp(ILOAD, 0)));
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
