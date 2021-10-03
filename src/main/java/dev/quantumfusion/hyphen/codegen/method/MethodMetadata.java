package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
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

	public abstract void writePut(MethodHandler mh);

	public abstract void writeGet(MethodHandler mh);

	public abstract void writeMeasure(MethodHandler mh);

	public abstract StringBuilder toFancyString(StringBuilder sb);

}
