package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.thr.exception.ScanException;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.MapUtil;
import dev.quantumfusion.hyphen.util.ReflectionUtil;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Type that takes part in the class hierarchy.
 * Either a normal class or a parameterized class, but no arrays.
 */
public class Clazz implements Clz {
	// The class "template"
	private final @Nullable Clazz template;
	// The raw class this represents
	final Class<?> clazz;

	// lazy
	private @Nullable Clazz superClass = null;
	private @Nullable Clazz[] superInterfaces = null;
	private @Nullable LinkedHashMap<String, ? extends AnnType> fields = null;

	Clazz(Clazz template, Class<?> clazz) {
		this.template = template;
		this.clazz = clazz;
	}

	/**
	 * Create a class for the raw class.
	 * <p /> Should be cached and be finalized by calling {@link #finish(AnnotatedType, Clazz)}
	 *
	 * @return either a Clazz or an ArrayClazz
	 */
	public static Clz createRawClazz(Class<?> clazz) {
		if (clazz.isArray()) return ArrayClazz.createRawArray();

		if (clazz.getTypeParameters().length > 0)
			return ParameterizedClazz.createRawParameterizedClass(clazz);

		return new Clazz(null, clazz);
	}

	/**
	 * Create a class for the raw class.
	 * <p /> Should be cached and be finalized by calling {@link #finish(AnnotatedType, Clazz)}
	 *
	 * @return either a Clazz, an ArrayClazz or a ParameterizedClazz
	 */
	public static Clz createRawClazz(AnnotatedType type) {
		return createRawClazz(ScanUtil.getClassFrom(type.getType()));
	}

	@Override
	public Clazz map(Clz other, Map<TypeClazz, TypeClazz> types, MergeDirection mergeDirection) {
		// validate if other is the same as us, or extends us
		if (this.equals(other)) return this;

		if (!(other instanceof Clazz otherClazz)) {
			Clz merge = other.map(this, types, mergeDirection.swap());
			if (merge instanceof Clazz mergeClazz) return mergeClazz;
			else return this;
		}

		if (!mergeDirection.isAssignable(this.clazz, otherClazz.clazz))
			throw new ScanException("Invalid type merge " + other);

		return otherClazz;
	}

	@Override
	public Clazz resolve(Clazz context) {
		return this;
	}

	public void finish(AnnotatedType type, Clazz source) {
		var clazz = ScanUtil.getClassFrom(type);
		this.superClass = Clazzifier.createClass(clazz.getGenericSuperclass(), this);
		this.superInterfaces = ArrayUtil.map(clazz.getGenericInterfaces(), Clazz[]::new, this, Clazzifier::createClass);

		// TODO: these shouldn't need to be cached, cause we are cached
		var classFields = ReflectionUtil.getClassFields(this.clazz);
		LinkedHashMap<String, AnnType> fields = new LinkedHashMap<>(classFields.length);

		for (Field classField : classFields) {
			if ((classField.getModifiers() & Opcodes.ACC_STATIC) != 0) continue;
			fields.put(classField.getName(), Clazzifier.createAnnotatedType(classField.getAnnotatedType(), this));
		}

		this.fields = fields;
	}

	public Class<?> pullClass() {
		return this.clazz;
	}

	public Class<?> pullBytecodeClass() {
		return this.clazz;
	}

	/**
	 * Get the Clz of a given type parameter
	 *
	 * @param type The name of the type parameter
	 * @return The type parameter, or {@link Undefined}
	 */
	public Clz resolveType(String type) {
		return Clazzifier.UNDEFINED;
	}

	@Override
	public String toString() {
		return this.clazz.getSimpleName();
	}

	@Override
	public boolean equals(Object o) {
		return this == o
				|| o instanceof Clazz that
				&& this.clazz.equals(that.clazz);

	}

	@Override
	public int hashCode() {
		return this.clazz.hashCode();
	}

	@Override
	public Clazz instantiate(AnnotatedType annotatedType) {
		return this;
	}

	public @Nullable Clazz getSuper() {
		Clazz superClass = this.superClass;
		if (superClass != null || this.template == null) return superClass;
		Clazz aSuper = this.template.getSuper();
		return this.superClass = aSuper == null
				? null
				: aSuper.instantiate(this.clazz.getAnnotatedSuperclass()).resolve(this);
	}

	private LinkedHashMap<String, ? extends AnnType> getFieldMap() {
		if (this.fields != null) return this.fields;
		assert this.template != null;
		return this.fields = (LinkedHashMap<String, AnnType>)
				MapUtil.mapValues(
						this.template.getFieldMap(),
						LinkedHashMap::new,
						annType -> annType.resolve(this));
	}

	public AnnType[] getFields() {
		return this.getFieldMap().values().toArray(AnnType[]::new);
	}
}
