package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.Constants;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.info.TypeInfo;

public class MethodCallDef implements SerializerDef {
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
		mh.callInternalStaticMethod(Constants.PUT_FUNC + this.info.getMethodName(false), null, mh.getIOClazz(), this.info.getClazz());
	}

	@Override
	public void doGet(MethodHandler mh) {
		mh.callInternalStaticMethod(Constants.GET_FUNC + this.info.getMethodName(false), this.info.getClazz(), mh.getIOClazz());
	}
}
