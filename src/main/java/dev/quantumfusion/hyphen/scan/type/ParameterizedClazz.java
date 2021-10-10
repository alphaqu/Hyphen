package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.thr.exception.ScanException;
import dev.quantumfusion.hyphen.util.AnnoUtil;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.CacheUtil;
import dev.quantumfusion.hyphen.util.ScanUtil;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static dev.quantumfusion.hyphen.scan.Clazzifier.UNDEFINED;

/**
 * Just like a Clazz, but it holds type parameters and its currently known definitions.
 */
public class ParameterizedClazz extends Clazz {
	private final Map<String, ? extends TypeClazz> types;

	private ParameterizedClazz(ParameterizedClazz template, Class<?> clazz, Map<String, ? extends TypeClazz> types) {
		super(template, clazz);
		types.forEach((s, t) -> t.setContext(this));
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
	public ParameterizedClazz instantiate(AnnotatedType type) {
		AnnotatedType[] typeParameters;
		if (type instanceof AnnotatedParameterizedType apt) {
			typeParameters = apt.getAnnotatedActualTypeArguments();
		} else if (type.getType() instanceof ParameterizedType pt) { // support wrapped annotation
			typeParameters = ArrayUtil.map(pt.getActualTypeArguments(), AnnotatedType[]::new, AnnoUtil::wrap);
		} else if (type.getType() instanceof Class<?> pt) { // raw
			typeParameters = new AnnotatedType[pt.getTypeParameters().length];
			Arrays.fill(typeParameters, AnnoUtil.WRAPPED_NULL);
		} else throw new IllegalArgumentException("" + type.getClass());

		int i = 0;
		Map<String, TypeClazz> newTypes = new LinkedHashMap<>(typeParameters.length);

		for (var t : this.types.values()) {
			AnnotatedType typeParameter = typeParameters[i++];
			AnnType annotatedType = Clazzifier.createAnnotatedType(typeParameter, this);
			newTypes.put(t.getName(), t.withActual(annotatedType));
		}

		ParameterizedClazz parameterizedClazz = new ParameterizedClazz(this, ScanUtil.getClassFrom(type), newTypes);
		return parameterizedClazz;
	}

	private final Map<Clazz, ParameterizedClazz> RESOLVE_CACHE = new HashMap<>();

	@Override
	public ParameterizedClazz resolve(Clazz context) {
		return CacheUtil.cache(this.RESOLVE_CACHE, context, (cont) -> {
			boolean mutated = false;
			Map<String, TypeClazz> newTypes = new LinkedHashMap<>(this.types.size());

			// if (!mutated) return this;

			ParameterizedClazz parameterizedClazz = new ParameterizedClazz(this, this.clazz, newTypes);

			for (var entry : this.types.entrySet()) {
				TypeClazz res;
				TypeClazz res1 = entry.getValue().resolveFUCKActual(cont);
				res1.finish(null, this);
				newTypes.put(entry.getKey(), res = res1);
				mutated |= res != entry.getValue();
			}

			return parameterizedClazz;
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
		this.types.forEach((s, type) -> sj.add(type.toString()));
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
	public Clazz merge(Clz other, Map<TypeClazz, TypeClazz> types, MergeDirection mergeDirection) {
		// TODO: do we need a direction here?


		// validate if other is the same as us, or extends us
		if (this.equals(other))
			return this;

		if (!(other instanceof ParameterizedClazz otherClazz)) {
			Clz merge = other.merge(other, types, mergeDirection.swap());
			if(merge instanceof Clazz mergeClazz) return mergeClazz;
			else return this;
		} else if (!mergeDirection.isAssignable(this.clazz, otherClazz.clazz))
			throw new ScanException("Invalid type merge");

		if (otherClazz.clazz.equals(this.clazz))
			return this.mergeSingle(otherClazz, types, mergeDirection);

		var baseClazz = mergeDirection.getBase(this, otherClazz);
		var subClazz = mergeDirection.getSub(this, otherClazz);

		Clazz aSuper = baseClazz.getSuper(subClazz);

		baseClazz.merge(aSuper, types, mergeDirection);

		var newTypes = new LinkedHashMap<String, TypeClazz>();


		ParameterizedClazz parameterizedClazz = new ParameterizedClazz(subClazz, subClazz.clazz, newTypes);

		// get all types
		subClazz.types.forEach((s, t) -> {
			TypeClazz type = types.getOrDefault(t, t);
			type.finish(null, parameterizedClazz);
			newTypes.put(s, type);
		});

		return parameterizedClazz;
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

	private ParameterizedClazz mergeSingle(Clazz otherClazz, Map<TypeClazz, TypeClazz> types, MergeDirection mergeDirection) {
		if (!(otherClazz instanceof ParameterizedClazz parameterizedClazz))
			throw new IllegalArgumentException("" + otherClazz.getClass());


		var newTypes = new LinkedHashMap<String, TypeClazz>();

		ParameterizedClazz parameterizedClazz1 = new ParameterizedClazz(this, this.clazz, newTypes);

		for (String s : this.types.keySet()) {
			TypeClazz ourType = this.types.get(s);
			TypeClazz otherType = parameterizedClazz.types.get(s);

			TypeClazz merge = ourType.merge(otherType, types, mergeDirection);
			merge.finish(null, parameterizedClazz1);
			newTypes.put(s, merge);
		}

		return parameterizedClazz1;
	}

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
