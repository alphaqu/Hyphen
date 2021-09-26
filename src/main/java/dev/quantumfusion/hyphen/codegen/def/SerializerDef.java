package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

public abstract class SerializerDef {

	public abstract Class<?> getType();

	public abstract void writePut(MethodHandler mh);

	public abstract void writeGet(MethodHandler mh);
}
