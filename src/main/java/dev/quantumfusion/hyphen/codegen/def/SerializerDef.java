package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

public interface SerializerDef {

	Class<?> getType();

	void doPut(MethodHandler mh);

	void doGet(MethodHandler mh);

	long getSize();

	default void calcSubSize(MethodHandler mh){
		throw new IllegalStateException("Shouldn't be called");
	}
}
