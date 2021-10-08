package dev.quantumfusion.hyphen.gen.impl;

import dev.quantumfusion.hyphen.gen.CodegenHandler;
import dev.quantumfusion.hyphen.gen.MethodHandler;
import dev.quantumfusion.hyphen.gen.MethodInfo;
import dev.quantumfusion.hyphen.gen.SerializerMethodDef;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.util.ReflectionUtil;

import java.lang.reflect.Field;

public class ClassDef extends SerializerMethodDef {
	public final Clazz clazz;
	public final Field[] classFields;

	public ClassDef(CodegenHandler handler, Clazz clazz, Field[] classFields) {
		super(handler);
		this.clazz = clazz;
		this.classFields = classFields;
	}

	public static ClassDef create(CodegenHandler handler, Clazz clazz) {
		final Field[] classFields = ReflectionUtil.getClassFields(clazz);
		for (Field classField : classFields) {

		}
		return null;
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
	public Class<?> getType() {
		return null;
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
