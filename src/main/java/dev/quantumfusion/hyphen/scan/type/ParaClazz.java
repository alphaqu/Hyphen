package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.scan.Direction;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class ParaClazz extends Clazz {
	public final Map<String, Clazz> parameters;

	public ParaClazz(@NotNull Class<?> aClass, Annotation[] sourceAnnotations, Annotation[] annotations, Map<String, Clazz> parameters) {
		super(aClass, sourceAnnotations, annotations);
		this.parameters = parameters;
	}

	public static ParaClazz create(AnnotatedType rawAnnotatedType, @Nullable Clazz ctx, Direction dir) {
		var parameters = new HashMap<String, Clazz>();
		var annotatedType = (AnnotatedParameterizedType) rawAnnotatedType;
		var type = (ParameterizedType) annotatedType.getType();
		var rawType = (Class<?>) type.getRawType();
		ArrayUtil.dualFor(annotatedType.getAnnotatedActualTypeArguments(), rawType.getTypeParameters(), (actual, internal) -> {
			parameters.put(internal.getTypeName(), Clazzifier.create((dir == Direction.SUB) ? ScanUtil.wrap(internal) : actual, ctx, dir));
		});
		return new ParaClazz(rawType, ScanUtil.parseAnnotations(ctx), annotatedType.getAnnotations(), parameters);
	}

	@Override
	public Clazz define(String typeName) {
		return parameters.getOrDefault(typeName, UnknownClazz.UNKNOWN);
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		ParaClazz paraClazz = (ParaClazz) o;
		return Objects.equals(parameters, paraClazz.parameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parameters);
	}
}
