package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

public interface SerializerDef {
	void writePut(MethodHandler mh, Runnable alloc);
	void writeGet(MethodHandler mh);
	void writeMeasure(MethodHandler mh, Runnable alloc);
}
