package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.io.UnsafeIO;

public class MethodCallDef extends SerializerDef {
	private final TypeInfo info;

	public MethodCallDef(TypeInfo info) {
		this.info = info;
	}

	@Override
	public Class<?> getType() {
		return this.info.clazz;
	}

	@Override
	public void doPut(MethodHandler mh) {
		mh.callInternalStaticMethod("encode_" + this.info.getMethodName(false), null, UnsafeIO.class, this.info.clazz);
	}

	@Override
	public void doGet(MethodHandler mh) {
		mh.callInternalStaticMethod("decode_" + this.info.getMethodName(false), this.info.clazz, UnsafeIO.class);
	}
}
