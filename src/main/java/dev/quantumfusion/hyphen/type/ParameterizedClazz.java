package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;
import dev.quantumfusion.hyphen.util.AnnoUtil;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.ReflectionUtil;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import static dev.quantumfusion.hyphen.Clazzifier.UNDEFINED;

/**
 * Just like a Clazz but it holds parameters and its currently known definitions.
 */
public class ParameterizedClazz extends Clazz {
	private final Map<String, ? extends TypeClazz> types;

	ParameterizedClazz(ParameterizedClazz template, Class<?> clazz, Map<String, ? extends TypeClazz> types) {
		super(template, clazz);
		this.types = types;
	}

	public static ParameterizedClazz createParameterizedClass(AnnotatedType type) {
		return createBaseParameterizedClass(((Class<?>) ((ParameterizedType) type.getType()).getRawType()));
	}

	public static ParameterizedClazz createBaseParameterizedClass(Class<?> type) {
		final Map<String, TypeClazz> types = new LinkedHashMap<>();

		for (var typeParameter : type.getTypeParameters()) {
			types.put(typeParameter.getName(), TypeClazz.createRaw(typeParameter));
		}

		return new ParameterizedClazz(null, type, types);
	}

	@Override
	public void finish(AnnotatedType type, Clazz source) {
		super.finish(type, source);
		int i = 0;
		TypeVariable<? extends Class<?>>[] typeParameters = this.clazz.getTypeParameters();
		for (TypeClazz t : this.types.values()) {
			t.resolveBounds(typeParameters[i++], this);
		}
	}

	@Override
	public ParameterizedClazz instantiate(AnnotatedType type) {
		AnnotatedType[] typeParameters;
		if (type instanceof AnnotatedParameterizedType apt) {
			typeParameters = apt.getAnnotatedActualTypeArguments();
		} else if (type.getType() instanceof ParameterizedType pt){ // support wrapped annotation
			typeParameters = ArrayUtil.map(pt.getActualTypeArguments(), AnnotatedType[]::new, AnnoUtil::wrap);
		} else throw new IllegalArgumentException();

		int i = 0;
		Map<String, TypeClazz> newTypes = new LinkedHashMap<>(typeParameters.length);
		for (var t : this.types.values()) {
			AnnotatedType typeParameter = typeParameters[i++];
			newTypes.put(t.getName(), t.withActual(Clazzifier.createAnnotatedType(typeParameter, this)));
		}
		return new ParameterizedClazz(this, Clazz.getClassFrom(type), newTypes);
	}

	@Override
	public Clazz resolve(Clazz source) {
		boolean mutated = false;
		Map<String, TypeClazz> newTypes = new LinkedHashMap<>(this.types.size());

		for(var entry : this.types.entrySet()) {
			TypeClazz res;
			newTypes.put(entry.getKey(), res = entry.getValue().resolveFUCKActual(source));
			mutated |= res != entry.getValue();
		}

		if(!mutated) return this;

		return new ParameterizedClazz(this, this.clazz, newTypes);
	}

	@Override
	public Clz resolveType(String type) {
		TypeClazz t = this.types.get(type);
		if(t == null) return UNDEFINED;
		return t;
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(",", "<", ">");
		this.types.forEach((s, type) -> sj.add(s + "=" + type.toString()));
		return super.toString() + sj;
	}

	@Override
	public boolean equals(Object o) {
		return this == o
				|| o instanceof ParameterizedClazz that
				&& super.equals(o)
				&& this.types.equals(that.types);

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + this.types.hashCode();
		return result;
	}
}
