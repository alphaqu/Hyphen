package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodMode;
import dev.quantumfusion.hyphen.info.TypeInfo;

public abstract class MethodMetadata {
	protected final TypeInfo info;

	public MethodMetadata(TypeInfo info) {
		this.info = info;
	}

	public TypeInfo getInfo() {
		return this.info;
	}

	public abstract void writePut(MethodHandler mh, MethodHandler.Var io, MethodHandler.Var data);

	public abstract void writeGet(MethodHandler mh, MethodHandler.Var io);

	public abstract void writeMeasure(MethodHandler mh, MethodHandler.Var data);

	public abstract long getSize();

	public void createPut(CodegenHandler ch) {
		try (MethodHandler mh = ch.createHyphenMethod(MethodMode.PUT, this)) {
			MethodHandler.Var io;
			MethodHandler.Var data;
			io = mh.createVar("io", ch.getIOMode().ioClass);
			data = mh.createVar("data", this.getInfo().getClazz());
			this.writePut(mh, io, data);
		}
	}

	public void createGet(CodegenHandler ch) {
		try (MethodHandler mh = ch.createHyphenMethod(MethodMode.GET, this)) {
			MethodHandler.Var io;
			io = mh.createVar("io", ch.getIOMode().ioClass);

			this.writeGet(mh, io);
		}
	}

	public void createMeasure(CodegenHandler ch) {
		if (this.getSize() >= 0) return; // skip

		try (MethodHandler mh = ch.createHyphenMethod(MethodMode.MEASURE, this)) {
			MethodHandler.Var data;
			data = mh.createVar("data", this.getInfo().getClazz());
			this.writeMeasure(mh, data);
			mh.returnOp();
		}
	}
}
