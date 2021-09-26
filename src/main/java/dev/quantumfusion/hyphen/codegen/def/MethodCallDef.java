package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.info.TypeInfo;

public class MethodCallDef extends SerializerDef {
	private final TypeInfo info;

	public MethodCallDef(TypeInfo info) {
		this.info = info;
	}

	@Override
	public Class<?> getType() {
		return info.clazz;
	}

	@Override
	public void writePut(MethodHandler mh) {

	}

	@Override
	public void writeGet(MethodHandler mh) {

	}
}
