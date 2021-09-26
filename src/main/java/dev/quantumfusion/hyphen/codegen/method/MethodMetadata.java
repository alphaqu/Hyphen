package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.info.TypeInfo;

public abstract class MethodMetadata {
	protected final TypeInfo info;

	public MethodMetadata(TypeInfo info) {
		this.info = info;
	}

	public abstract void writePut(MethodHandler mh);

	public abstract void writeGet(MethodHandler mh);
}
