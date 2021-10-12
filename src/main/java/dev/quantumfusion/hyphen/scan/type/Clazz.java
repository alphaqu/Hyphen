package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.thr.exception.ScanException;
import dev.quantumfusion.hyphen.util.java.ArrayUtil;
import dev.quantumfusion.hyphen.util.java.MapUtil;
import dev.quantumfusion.hyphen.util.java.ReflectionUtil;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

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
	private boolean initialized = false;
	private @Nullable Clazz superClass = null;
	private @Nullable Clazz[] superInterfaces = null;
	private @Nullable LinkedHashMap<String, ? extends FieldType> fields = null;

	Clazz(Clazz template, Class<?> clazz) {
		this.template = template;
		this.clazz = clazz;
	}

	public static Clz createRawClazz(Class<?> clazz) {
		if (clazz.isArray()) return ArrayClazz.createRawArray();

		if (clazz.getTypeParameters().length > 0)
			return ParameterizedClazz.createRawParameterizedClass(clazz);

		return new Clazz(null, clazz);
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

	private void init() {
		if (initialized) return;
		initialized = true;

		if (this.template == null) {
			this.superClass = Clazzifier.createClass(this.clazz.getGenericSuperclass(), this);
			this.superInterfaces = ArrayUtil.map(this.clazz.getGenericInterfaces(), Clazz[]::new, this, Clazzifier::createClass);

			// TODO: these shouldn't need to be cached, cause we are cached
			var classFields = ReflectionUtil.getClassFields(this.clazz);
			LinkedHashMap<String, FieldType> fields = new LinkedHashMap<>(classFields.length);

			for (Field classField : classFields) {
				if ((classField.getModifiers() & Opcodes.ACC_STATIC) != 0) continue;
				fields.put(classField.getName(), Clazzifier.createAnnotatedType(classField.getAnnotatedType(), this));
			}

			this.fields = fields;
		} else {
			Clazz aSuper = this.template.getSuper();
			this.superClass = aSuper == null ? null : aSuper.resolve(this);
			this.superInterfaces = ArrayUtil.map(this.template.getInterfaces(), Clazz[]::new, c -> c.resolve(this));
			this.fields = MapUtil.mapValues(
					this.template.getFieldMap(),
					LinkedHashMap::new,
					c -> c.resolve(this));
		}
	}

	public Class<?> pullClass() {
		return this.clazz;
	}

	@Override
	public Class<?> pullBytecodeClass() {
		return this.clazz;
	}

	/**
	 * Get the Clz of a given type parameter
	 *
	 * @param type The name of the type parameter
	 * @return The type parameter, or null
	 */
	public @Nullable TypeClazz resolveType(String type) {
		return null;
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
		this.init();
		return this.superClass;
	}

	public Clazz[] getInterfaces() {
		this.init();
		return this.superInterfaces;
	}

	// should only be called by the clazzifier
	public LinkedHashMap<String, ? extends FieldType> getFieldMap() {
		this.init();
		return this.fields;
	}

	@Deprecated
	public FieldType[] getFields() {
		return this.getFieldMap().values().toArray(FieldType[]::new);
	}
}
