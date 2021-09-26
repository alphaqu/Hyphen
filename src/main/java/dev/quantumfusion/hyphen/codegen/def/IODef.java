package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

public class IODef extends SerializerDef {
	private final Class<?> clazz;

	public IODef(Class<?> clazz) {
		this.clazz = clazz;

	}

	@Override
	public Class<?> getType() {
		return clazz;
	}

	@Override
	public void writePut(MethodHandler mh) {

	}

	@Override
	public void writeGet(MethodHandler mh) {

	}
}
