package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Direction;
import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.ScanUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ParaClazz extends Clazz {
	public final Map<String, Clazz> parameters;

	protected ParaClazz(Class<?> aClass, Map<String, Clazz> parameters, Annotation[] annotations) {
		super(aClass, annotations);
		this.parameters = parameters;
	}

	public static ParaClazz create(AnnotatedType rawAnnotatedType, Clazz ctx, Direction dir) {
		var parameters = new HashMap<String, Clazz>();
		var annotatedType = (AnnotatedParameterizedType) rawAnnotatedType;
		var type = (ParameterizedType) annotatedType.getType();
		var rawType = (Class<?>) type.getRawType();
		ArrayUtil.dualFor(annotatedType.getAnnotatedActualTypeArguments(), rawType.getTypeParameters(), (actual, internal) -> {
			parameters.put(internal.getTypeName(), ScanHandler.create((dir == Direction.SUB) ? ScanUtil.wrap(internal) : actual, ctx, dir));
		});
		return new ParaClazz(rawType, parameters, annotatedType.getAnnotations());
	}

	@Override
	public Clazz define(String typeName) {
		return parameters.getOrDefault(typeName, ScanHandler.UNKNOWN);
	}

	@Override
	public int defined() {
		int i = 1;
		for (Clazz value : parameters.values()) i += value.defined();
		return i;
	}

	@Override
	public String toString() {
		var sj = new StringJoiner(", ", "<", ">");
		parameters.forEach((s, clazz) -> sj.add(s + " = " + clazz.toString()));
		return super.toString() + sj;
	}
}
