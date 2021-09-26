package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.info.ArrayInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;

public class ArrayMethod extends MethodMetadata {
	private final TypeInfo values;

	private ArrayMethod(ArrayInfo info) {
		super(info);
		this.values = info.values;
	}

	public static ArrayMethod create(ArrayInfo info, ScanHandler scanHandler) {
		scanHandler.createSerializeMetadata(info.values);
		return new ArrayMethod(info);
	}

	@Override
	public void writePut(MethodHandler mh) {

	}

	@Override
	public void writeGet(MethodHandler mh) {

	}
}
