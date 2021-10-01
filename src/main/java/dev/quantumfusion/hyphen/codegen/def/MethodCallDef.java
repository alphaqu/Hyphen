package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
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
		this.serializeMethod.callPut(mh);
	}

	@Override
	public void doGet(MethodHandler mh) {
		this.serializeMethod.callGet(mh);
	}

	@Override
	public long getSize() {
		return this.serializeMethod.getSize();
	}

	@Override
	public void calcSubSize(MethodHandler mh) {
		this.serializeMethod.callSubCalcSize(mh);
	}
}
