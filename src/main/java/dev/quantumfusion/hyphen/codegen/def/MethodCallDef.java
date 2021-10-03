package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodType;
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
	public void writePut(MethodHandler mh) {
		mh.callHyphenMethod(MethodType.PUT, serializeMethod);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.callHyphenMethod(MethodType.GET, serializeMethod);
	}

	@Override
	public void writeMeasure(MethodHandler mh) {
		mh.callHyphenMethod(MethodType.MEASURE, serializeMethod);
	}

	@Override
	public boolean needsFieldOnMeasure() {
		return this.serializeMethod.dynamicSize();
	}

	@Override
	public StringBuilder toFancyString(StringBuilder sb) {
		return sb.append("CALL{").append(this.info.toFancyString()).append("}");
	}
}
