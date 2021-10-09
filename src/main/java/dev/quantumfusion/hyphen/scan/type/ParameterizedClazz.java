package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.thr.ScanException;
import dev.quantumfusion.hyphen.util.AnnoUtil;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.CacheUtil;
import dev.quantumfusion.hyphen.util.ScanUtil;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import static dev.quantumfusion.hyphen.scan.Clazzifier.UNDEFINED;

/**
 * Just like a Clazz, but it holds type parameters and its currently known definitions.
 */
public class ParameterizedClazz extends Clazz {
	private final Map<String, ? extends TypeClazz> types;

	private ParameterizedClazz(ParameterizedClazz template, Class<?> clazz, Map<String, ? extends TypeClazz> types) {
		super(template, clazz);
		this.types = types;
	}

	/**
	 * Create a parameterized clazz for the raw class.
	 * <p /> Should be cached and be finalized by calling {@link #finish(AnnotatedType, Clazz)}
	 */
	public static ParameterizedClazz createRawParameterizedClass(AnnotatedType type) {
		return createRawParameterizedClass(ScanUtil.getClassFrom(type));
	}

	/**
	 * Create a parameterized clazz for the raw class.
	 * <p /> Should be cached and be finalized by calling {@link #finish(AnnotatedType, Clazz)}
	 */
	public static ParameterizedClazz createRawParameterizedClass(Class<?> type) {
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
			t.finish(null, this);
		}
	}

	@Override
	public ParameterizedClazz instantiate(AnnotatedType type) {
		AnnotatedType[] typeParameters;
		if (type instanceof AnnotatedParameterizedType apt) {
			typeParameters = apt.getAnnotatedActualTypeArguments();
		} else if (type.getType() instanceof ParameterizedType pt) { // support wrapped annotation
			typeParameters = ArrayUtil.map(pt.getActualTypeArguments(), AnnotatedType[]::new, AnnoUtil::wrap);
		} else throw new IllegalArgumentException();

		int i = 0;
		Map<String, TypeClazz> newTypes = new LinkedHashMap<>(typeParameters.length);
		for (var t : this.types.values()) {
			AnnotatedType typeParameter = typeParameters[i++];
			newTypes.put(t.getName(), t.withActual(Clazzifier.createAnnotatedType(typeParameter, this)));
		}
		return new ParameterizedClazz(this, ScanUtil.getClassFrom(type), newTypes);
	}

	private final Map<Clazz, ParameterizedClazz> RESOLVE_CACHE = new HashMap<>();

	@Override
	public ParameterizedClazz resolve(Clazz context) {
		return CacheUtil.cache(this.RESOLVE_CACHE, context, (cont) -> {
			boolean mutated = false;
			Map<String, TypeClazz> newTypes = new LinkedHashMap<>(this.types.size());

			for (var entry : this.types.entrySet()) {
				TypeClazz res;
				newTypes.put(entry.getKey(), res = entry.getValue().resolveFUCKActual(cont));
				mutated |= res != entry.getValue();
			}

			if (!mutated) return this;

			return new ParameterizedClazz(this, this.clazz, newTypes);
		});
	}

	@Override
	public Clz resolveType(String type) {
		TypeClazz t = this.types.get(type);
		if (t == null) return UNDEFINED;
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

	@Override
	public Clazz merge(Clz other, Map<TypeClazz, TypeClazz> types) {
		// TODO: do we need a direction here?


		// validate if other is the same as us, or extends us
		if (this.equals(other))
			return this;

		if (!(other instanceof Clazz otherClazz) || !this.clazz.isAssignableFrom(otherClazz.clazz))
			throw new ScanException("Invalid type merge");

		if (otherClazz.clazz.equals(this.clazz))
			return this.mergeSingle(otherClazz, types);

		Clazz aSuper = this.getSuper(otherClazz);

		aSuper.merge(other, types);

		var newTypes = new LinkedHashMap<String, TypeClazz>();

		// get all types
		this.types.forEach((s, t) -> newTypes.put(s, types.getOrDefault(t, t)));

		if(newTypes.equals(this.types))
			return this; // no need to reallocated

		return new ParameterizedClazz(this, this.clazz, newTypes);
	}

	private Clazz getSuper(Clazz otherClazz) {
		if (this.clazz.equals(otherClazz.clazz)) {
			return this;
		}

		Clazz aSuper = otherClazz.getSuper();
		if (aSuper == null)
			throw new UnsupportedOperationException("Interface merging hasn't been implemented");

		return this.getSuper(aSuper);
	}

	@Override
	protected ParameterizedClazz mergeSingle(Clazz otherClazz, Map<TypeClazz, TypeClazz> types) {
		if (!(otherClazz instanceof ParameterizedClazz parameterizedClazz))
			return this; // merging with a raw class like ArrayList<Integer> <-> ArrayList


		var newTypes = new LinkedHashMap<String, TypeClazz>();

		for (String s : this.types.keySet()) {
			TypeClazz ourType = this.types.get(s);
			TypeClazz otherType = parameterizedClazz.types.get(s);

			newTypes.put(s, ourType.merge(otherType, types));
		}

		if (this.types.equals(newTypes))
			return this; // no need to reallocated

		return new ParameterizedClazz(this, this.clazz, newTypes);
	}

	@Override
	protected ParameterizedClazz mergeSubclass(Clazz otherClazz, Map<TypeClazz, TypeClazz> types) {
		if (!(otherClazz instanceof ParameterizedClazz parameterizedClazz)) {
			// merging with a class like C1<T> <-> IntC1
			// TODO: validate types
		} else {
			// TODO?
		}

		return null;
	}
}
