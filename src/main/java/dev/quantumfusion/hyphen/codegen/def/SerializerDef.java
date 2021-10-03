package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

public interface SerializerDef {

	Class<?> getType();

	void writePut(MethodHandler mh);

	void writeGet(MethodHandler mh);

	void writeMeasure(MethodHandler mh);

	boolean needsFieldOnMeasure();

	StringBuilder toFancyString(StringBuilder sb);
}
