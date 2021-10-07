package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.ReflectionUtil;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.*;
import java.util.LinkedHashMap;

/**
 * A Type that takes part in the class hierarchy.
 * Either a normal class or a parameterized class, but no arrays.
 */
public class Clazz implements Clz {
	static public int allocations = 0;
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

		allocations++;
	}

	/**
	 * Create a class for the raw class.
	 * <p /> Should be cached and be finalized by calling {@link #finish(AnnotatedType, Clazz)}
	 * @return either a Clazz or an ArrayClazz
	 */
	public static Clz createRawClazz(Class<?> clazz) {
		if (clazz.isArray()) {
			return ArrayClazz.createRawArray();
		}

		return new Clazz(null, clazz);
	}

	/**
	 * Create a class for the raw class.
	 * <p /> Should be cached and be finalized by calling {@link #finish(AnnotatedType, Clazz)}
	 * @return either a Clazz or an ArrayClazz
	 */
	public static Clz createRawClazz(AnnotatedType type) {
		return createRawClazz(getClassFrom(type.getType()));
	}

	public void finish(AnnotatedType type, Clazz source) {
		var clazz = getClassFrom(type);
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

	// TODO: move to util?
	public static Class<?> getClassFrom(AnnotatedType type) {
		return getClassFrom(type.getType());
	}

	// TODO: move to util?
	public static Class<?> getClassFrom(Type type) {
		if (type instanceof Class<?> c) return c;
		if (type instanceof ParameterizedType pt) return getClassFrom(pt.getRawType());
		if (type instanceof GenericArrayType gat) return getClassFrom(gat.getGenericComponentType());
		throw new IllegalArgumentException(type.getClass() + ": " + type);
	}

	@Override
	public Clazz resolve(Clazz context) {
		return this;
	}

	public Class<?> pullClass() {
		return this.clazz;
	}

	public Class<?> pullBytecodeClass() {
		return this.clazz;
	}

	/*public Clazz getSub(Class<?> clazz) {
		return Clazzifier.create(AnnoUtil.wrap(clazz), this);
	}*/

	/**
	 * Get the Clz of a given type parameter
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
		return this.superClass = aSuper == null ? null : aSuper.instantiate(this.clazz.getAnnotatedSuperclass()).resolve(this);
	}

	private LinkedHashMap<String, ? extends AnnType> getFieldMap() {
		if (this.fields != null) return this.fields;
		assert this.template != null;
		var templateFieldMap = this.template.getFieldMap();
		var fieldMap = new LinkedHashMap<String, AnnType>(templateFieldMap.size());

		templateFieldMap.forEach((name, ann) -> fieldMap.put(name, ann.resolve(this)));

		return this.fields = fieldMap;
	}

	public AnnType[] getFields() {
		return this.getFieldMap().values().toArray(AnnType[]::new);
	}
}
