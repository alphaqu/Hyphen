package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;
import dev.quantumfusion.hyphen.util.AnnoUtil;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Just like a Clazz but it holds parameters and its currently known definitions.
 */
public class ParameterizedClazz extends Clazz {
	public final Map<String, Clazz> types;

	public ParameterizedClazz(Class<?> clazz, Map<Class<? extends Annotation>, Annotation> annotations, Map<Class<? extends Annotation>, Annotation> globalAnnotations, Map<String, Clazz> types) {
		super(clazz, annotations, globalAnnotations);
		this.types = types;
	}

	public static ParameterizedClazz mapForward(AnnotatedType t, Clazz parent) {
		AnnotatedParameterizedType type = (AnnotatedParameterizedType) t;
		final Map<String, Clazz> types = new HashMap<>();
		final Class<?> rawType = (Class<?>) ((ParameterizedType) type.getType()).getRawType();
		ArrayUtil.dualForEach(rawType.getTypeParameters(), type.getAnnotatedActualTypeArguments(), (internalArg, typeArg, i) -> {
			final String internalName = internalArg.getName();
			if (typeArg instanceof AnnotatedTypeVariable typeVariable)
				types.put(internalName, parent.defineType(typeVariable.getType().getTypeName()));
			else types.put(internalName, Clazzifier.create(typeArg, parent));
		});

		return new ParameterizedClazz(rawType, AnnoUtil.parseAnnotations(t), ReflectionUtil.getClassAnnotations(parent), types);
	}

	@Override
	public Clazz defineType(String type) {
		return types.get(type);
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(",", "<", ">");
		types.forEach((s, type) -> sj.add(s + "=" + type.toString()));
		return super.toString() + sj;
	}
}
