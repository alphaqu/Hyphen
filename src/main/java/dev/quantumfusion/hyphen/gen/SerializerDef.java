package dev.quantumfusion.hyphen.gen;

public interface SerializerDef {
	Class<?> getType();
	void writeGet(MethodHandler mh);
	void writePut(MethodHandler mh);
	void writeMeasure(MethodHandler mh);
}
