package dev.quantumfusion.hyphen.gen.impl;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.gen.*;
import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.util.java.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ClassDef extends SerializerMethodDef {
	public final Clazz clazz;
	public final Map<Field, SerializerDef> fields;

	public ClassDef(SerializerHandler sh, Clazz clazz) {
		super(sh, clazz);
		this.clazz = clazz;
		this.fields = new HashMap<>();

		// create fields
		for (Field classField : ReflectionUtil.getClassFields(clazz)) {
			this.fields.put(classField, sh.acquireDef(Clazzifier.create(classField.getAnnotatedType(), clazz)));
		}

	}

	@Override
	public Class<?> getType() {
		return clazz.pullClass();
	}

	@Override
	public void writeGet(MethodHandler mh) {

	}

	@Override
	public void writeGetMethod(MethodHandler mh) {

	}

	@Override
	public void writePut(MethodHandler mh) {

	}

	@Override
	public void writePutMethod(MethodHandler mh) {

	}

	@Override
	public void writeMeasure(MethodHandler mh) {

	}

	@Override
	public void writeMeasureMethod(MethodHandler mh) {

	}
}
