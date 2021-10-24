package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

public interface SerializerDef {
	void writePut(MethodHandler mh, Runnable valueLoad);

	void writeGet(MethodHandler mh);

	default void writeMeasure(MethodHandler mh, Runnable valueLoad) {
		throw new UnsupportedOperationException();
	}

	default int staticSize() {
		return 0;
	}

	default boolean hasDynamicSize() {
		return true;
	}
}
