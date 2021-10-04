package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;
import dev.quantumfusion.hyphen.util.ArrayUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ParameterizedClazz extends Clazz {
	public final Map<String, Clazz> types;

	protected ParameterizedClazz(Class<?> clazz, Map<String, Clazz> types) {
		super(clazz);
		this.types = types;
	}

	public static ParameterizedClazz mapForward(Type t, Clazz parent) {
		ParameterizedType type = (ParameterizedType) t;
		final Map<String, Clazz> types = new HashMap<>();
		final Class<?> rawType = (Class<?>) type.getRawType();
		ArrayUtil.dualForEach(rawType.getTypeParameters(), type.getActualTypeArguments(), (internalArg, typeArg, i) -> {
			final String internalName = internalArg.getName();
			if (typeArg instanceof TypeVariable typeVariable)
				types.put(internalName, parent.defineType(typeVariable.getTypeName()));
			else types.put(internalName, Clazzifier.create(typeArg, parent));
		});

		return new ParameterizedClazz(rawType, types);
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
