package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.scan.Direction;
import dev.quantumfusion.hyphen.thr.UnknownTypeException;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class ParaClazz extends Clazz {
	public final Map<String, Clazz> parameters;

	public ParaClazz(SerializerHandler<?, ?> handler, @NotNull Class<?> aClass, Map<Class<? extends Annotation>, Object> annotations, Map<String, Clazz> parameters) {
		super(handler, aClass, annotations);
		this.parameters = parameters;
	}

	public static ParaClazz create(SerializerHandler<?, ?> handler, AnnotatedType rawAnnotatedType, @Nullable Clazz ctx, Direction dir) {
		var parameters = new HashMap<String, Clazz>();
		var rawType = ScanUtil.getClassFrom(rawAnnotatedType);

		AnnotatedType annType;

		if (rawAnnotatedType instanceof ScanUtil.FieldAnnotatedType fieldAnnotatedType)
			annType = fieldAnnotatedType.annotatedType();
		else
			annType = rawAnnotatedType;

		if (annType instanceof AnnotatedParameterizedType annotatedType)
			ArrayUtil.dualFor(annotatedType.getAnnotatedActualTypeArguments(), rawType.getTypeParameters(), (actual, internal) -> {
				final String key = (dir == Direction.SUB) ? actual.getType().getTypeName() : internal.getTypeName();
				final Clazz value = Clazzifier.create(handler, (dir == Direction.SUB) ? ScanUtil.wrap(internal) : actual, ctx, dir);
				parameters.put(key, value);
			});
		else {
			if (dir != Direction.SUB)
				throw new UnknownTypeException("Class with parameters comes from a non parameterized source.",
											   "Check if you forgot to declare the parameters and left the type raw in any of the fields.");
			for (var typeParameter : rawType.getTypeParameters())
				parameters.put(typeParameter.getTypeName(), Clazzifier.create(handler, ScanUtil.wrap(typeParameter), ctx, dir));
		}

		return new ParaClazz(handler, rawType, ScanUtil.acquireAnnotations(handler, rawAnnotatedType, ctx), parameters);
	}

	@Override
	public Clazz define(String typeName) {
		final Clazz orDefault = parameters.getOrDefault(typeName, UnknownClazz.UNKNOWN);
		if (orDefault == UnknownClazz.UNKNOWN) {
			for (Clazz value : parameters.values()) {
				final Clazz define = value.define(typeName);
				if (define != UnknownClazz.UNKNOWN) return define;
			}
		}
		return orDefault;
	}

	@Override
	public int defined() {
		int i = 1;
		for (Clazz value : parameters.values()) i += value.defined();
		return i;
	}

	@Override
	public String toString() {
		var sj = new StringJoiner(", ", "{", "}");
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
