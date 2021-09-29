package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

public class StaleDef extends SerializerDef {

	public final Class<?> clazz;

	public StaleDef(Class<?> clazz) {
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
