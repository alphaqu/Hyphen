package dev.quantumfusion.hyphen.codegen;

public interface SerializerDef {
	void writeGet(MethodHandler mh);
	void writePut(MethodHandler mh);
	void writeMeasure(MethodHandler mh);
}
