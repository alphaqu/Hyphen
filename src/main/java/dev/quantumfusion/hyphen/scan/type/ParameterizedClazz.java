package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.thr.exception.ScanException;
import dev.quantumfusion.hyphen.util.CacheUtil;
import dev.quantumfusion.hyphen.util.java.MapUtil;
import dev.quantumfusion.hyphen.util.ScanUtil;

import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import static dev.quantumfusion.hyphen.scan.Clazzifier.UNDEFINED;

/**
 * Just like a Clazz, but it holds type parameters and its currently known definitions.
 */
public class ParameterizedClazz extends Clazz {
	private final Map<Clazz, ParameterizedClazz> RESOLVE_CACHE = new HashMap<>();
	private final Map<String, ? extends TypeClazz> types;


	private ParameterizedClazz(ParameterizedClazz template, Class<?> clazz, Map<String, ? extends TypeClazz> types) {
		super(template, clazz);
		this.types = types;
		this.types.forEach((s, t) -> t.setContext(this));
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

		for (var typeParameter : type.getTypeParameters())
			types.put(typeParameter.getName(), TypeClazz.createRaw(typeParameter));

		return new ParameterizedClazz(null, type, types);
	}

	@Override
	public ParameterizedClazz instantiate(AnnotatedType type) {
		var typeParameters = ScanUtil.getAnnotatedTypesArguments(type);

		var newTypes = new LinkedHashMap<String, TypeClazz>(typeParameters.length);
		MapUtil.forEachIndex(this.types, (key, value, i1) -> {
			newTypes.put(value.name, value.withActual(Clazzifier.createAnnotatedType(typeParameters[i1], this)));
		});

		return new ParameterizedClazz(this, ScanUtil.getClassFrom(type), newTypes);
	}

	@Override
	public Clazz map(Clz other, Map<TypeClazz, TypeClazz> types, MergeDirection mapDirection) {
		// validate if other is the same as us, or extends us
		if (this.equals(other)) return this;

		if (!(other instanceof ParameterizedClazz otherClazz)) {
			Clz merge = other.map(other, types, mapDirection.swap());
			if (merge instanceof Clazz mergeClazz) return mergeClazz;
			else return this;
		} else if (!mapDirection.isAssignable(this.clazz, otherClazz.clazz))
			throw new ScanException("Invalid type merge");

		if (otherClazz.clazz.equals(this.clazz))
			return this.mapSameClass(otherClazz, types, mapDirection);

		var baseClazz = mapDirection.getBase(this, otherClazz);
		var subClazz = mapDirection.getSub(this, otherClazz);

		Clazz aSuper = baseClazz.getSuper(subClazz);

		baseClazz.map(aSuper, types, mapDirection);


		// get all types
		var newTypes = new LinkedHashMap<String, TypeClazz>();
		subClazz.types.forEach((s, t) -> {
			TypeClazz type = types.getOrDefault(t, t);
			newTypes.put(s, type);
		});

		return new ParameterizedClazz(subClazz, subClazz.clazz, newTypes);
	}

	@Override
	public ParameterizedClazz resolve(Clazz context) {
		return CacheUtil.cache(this.RESOLVE_CACHE, context, (ctx) -> {
			var newTypes = new LinkedHashMap<String, TypeClazz>(this.types.size());
			var mutated = false;

			for (var entry : this.types.entrySet()) {
				var value = entry.getValue();
				var res = value.resolveFUCKActual(ctx);
				newTypes.put(entry.getKey(), res);
				mutated |= res != value;
			}

			if (!mutated) return this;
			return new ParameterizedClazz(this, this.clazz, newTypes);
		});
	}

	@Override
	public Clz resolveType(String type) {
		var t = this.types.get(type);
		return t == null ? UNDEFINED : t;
	}

	private Clazz getSuper(Clazz otherClazz) {
		if (this.clazz.equals(otherClazz.clazz)) {
			return otherClazz;
		}

		Clazz aSuper = otherClazz.getSuper();
		if (aSuper == null)
			throw new UnsupportedOperationException("Interface merging hasn't been implemented");

		return this.getSuper(aSuper);
	}

	private ParameterizedClazz mapSameClass(ParameterizedClazz otherClazz, Map<TypeClazz, TypeClazz> types, MergeDirection mergeDirection) {
		assert this.clazz.equals(otherClazz.clazz);

		var newTypes = new LinkedHashMap<String, TypeClazz>();

		for (String s : this.types.keySet()) {
			TypeClazz ourType = this.types.get(s);
			TypeClazz otherType = otherClazz.types.get(s);

			TypeClazz merge = ourType.map(otherType, types, mergeDirection);
			newTypes.put(s, merge);
		}

		return new ParameterizedClazz(this, this.clazz, newTypes);
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(",", "<", ">");
		this.types.forEach((s, type) -> sj.add(type.toString()));
		return super.toString() + sj;
	}

	@Override
	public boolean equals(Object o) {
		return this == o || o instanceof ParameterizedClazz that && super.equals(o) && this.types.equals(that.types);

	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + this.types.hashCode();
	}
}
