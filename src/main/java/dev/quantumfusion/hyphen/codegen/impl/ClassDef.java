package dev.quantumfusion.hyphen.codegen.impl;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodInfo;
import dev.quantumfusion.hyphen.codegen.SerializerMethodDef;

import static org.objectweb.asm.Opcodes.ILOAD;

public class ClassDef extends SerializerMethodDef {
	public ClassDef(CodegenHandler handler) {
		super(handler);
	}

	@Override
	public void writeGetMethod(MethodHandler mh) {

	}

	@Override
	public void writePutMethod(MethodHandler mh) {

	}

	@Override
	public void writeMeasureMethod(MethodHandler mh) {

	}

	@Override
	public void writeGet(MethodHandler mh) {

	}

	@Override
	public void writePut(MethodHandler mh) {

	}

	@Override
	public void writeMeasure(MethodHandler mh) {

	}

	@Override
	public MethodInfo getMethodInfo() {
		return new MethodInfo("ClassG", Object.class);
	}


	@Override
	public MethodInfo putMethodInfo() {
		return new MethodInfo("ClassP", Object.class);
	}

	@Override
	public MethodInfo measureMethodInfo() {
		return new MethodInfo("ClassM", Object.class);
	}
}
