package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodMode;
import dev.quantumfusion.hyphen.info.TypeInfo;

public abstract class MethodMetadata<T extends TypeInfo> {
	protected final T info;

	public MethodMetadata(T info) {
		this.info = info;
	}

	public T getInfo() {
		return this.info;
	}

	public abstract long getSize();

	public abstract boolean dynamicSize();

	public abstract void writePut(MethodHandler mh, MethodHandler.Var io, MethodHandler.Var data);

	public abstract void writeGet(MethodHandler mh, MethodHandler.Var io);

	public abstract void writeMeasure(MethodHandler mh, MethodHandler.Var data);


	public void createPut(CodegenHandler ch) {
		try (MethodHandler mh = ch.createHyphenMethod(MethodMode.PUT, this)) {
			MethodHandler.Var io = mh.createVar("io", ch.getIOMode().ioClass);
			MethodHandler.Var data = mh.createVar("data", this.getInfo().getClazz());
			this.writePut(mh, io, data);
		}
	}

	public void createGet(CodegenHandler ch) {
		try (MethodHandler mh = ch.createHyphenMethod(MethodMode.GET, this)) {
			MethodHandler.Var io = mh.createVar("io", ch.getIOMode().ioClass);
			this.writeGet(mh, io);
		}
	}

	public void createMeasure(CodegenHandler ch) {
		if (this.dynamicSize()) {
			try (MethodHandler mh = ch.createHyphenMethod(MethodMode.MEASURE, this)) {
				MethodHandler.Var data = mh.createVar("data", this.getInfo().getClazz());
				this.writeMeasure(mh, data);
			}
		}
	}

	public abstract StringBuilder toFancyString(StringBuilder sb);

}
