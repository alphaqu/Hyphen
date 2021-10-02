package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodMode;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;

public class MethodCallDef implements SerializerDef {
	private final TypeInfo info;
	private final MethodMetadata serializeMethod;

	public MethodCallDef(TypeInfo info, MethodMetadata serializeMethod) {
		this.info = info;
		this.serializeMethod = serializeMethod;
	}

	@Override
	public Class<?> getType() {
		return this.info.getClazz();
	}

	@Override
	public void doPut(MethodHandler mh) {
		mh.callHyphenMethod(MethodMode.PUT, serializeMethod);
	}

	@Override
	public void doGet(MethodHandler mh) {
		mh.callHyphenMethod(MethodMode.GET, serializeMethod);
	}


	@Override
	public boolean needsField() {
		return this.serializeMethod.dynamicSize();
	}

	@Override
	public void doMeasure(MethodHandler mh) {
		mh.callHyphenMethod(MethodMode.MEASURE, serializeMethod);
	}
}
