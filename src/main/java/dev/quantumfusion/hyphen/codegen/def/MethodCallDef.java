package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.Constants;
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
		return this.info.getClazz();
	}

	@Override
	public void doPut(MethodHandler mh) {
		mh.callInternalStaticMethod(Constants.PUT_FUNC + this.info.getMethodName(false), null, UnsafeIO.class, this.info.getClazz());
	}

	@Override
	public void doGet(MethodHandler mh) {
		mh.callInternalStaticMethod(Constants.GET_FUNC + this.info.getMethodName(false), this.info.getClazz(), UnsafeIO.class);
	}
}
