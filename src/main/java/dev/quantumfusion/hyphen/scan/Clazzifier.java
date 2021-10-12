package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.thr.exception.ScanException;
import dev.quantumfusion.hyphen.scan.type.*;
import dev.quantumfusion.hyphen.util.*;
import dev.quantumfusion.hyphen.util.java.ArrayUtil;
import dev.quantumfusion.hyphen.util.java.ReflectionUtil;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * The main type handling class. This is where you get your fields and Hyphen scans for implementations.
 */
public class Clazzifier {
	public static final Clz UNDEFINED = Undefined.UNDEFINED;
	public static final FieldType UNDEFINED_FIELD = FieldType.of(Undefined.UNDEFINED);
	private static final List<ClzCreator> FORWARD_CLAZZERS = new ArrayList<>();
	private static final Map<Class<?>, Clz> CLASS_CACHE = new IdentityHashMap<>();
	private static final Map<Clazz, FieldType[]> ALL_FIELD_CACHE = new IdentityHashMap<>();

	static {
		FORWARD_CLAZZERS.add(ClzCreator
				.ofC(ParameterizedType.class, ParameterizedClazz::createRawParameterizedClass)
				.cachedOrPostProcess(ScanUtil::getClassFrom, ParameterizedClazz::finish));
		FORWARD_CLAZZERS.add(ClzCreator
				.ofT(TypeVariable.class, TypeClazz::createRaw)
				.postProcess(TypeClazz::finish)
		);
		FORWARD_CLAZZERS.add(ClzCreator
				.of(GenericArrayType.class, ArrayClazz::createRawArray)
				.postProcess(Clz::finish)
		);
		FORWARD_CLAZZERS.add(ClzCreator.of(WildcardType.class, () -> UNDEFINED));
		FORWARD_CLAZZERS.add(ClzCreator
				.ofC(Class.class, Clazz::createRawClazz)
				.cachedOrPostProcess(ScanUtil::getClassFrom, Clz::finish)
		);
	}


	/**
	 * This creates a {@link Clazz} from an AnnotatedType.<br> This method is cached and the actual implementations is in the {@link Clazzifier#createFromType}
	 *
	 * @param annotatedType An {@link AnnotatedType} that you want to create a {@link Clazz} from
	 * @param source        The source of the AnnotatedType. Used for mapping Class Parameters.
	 * @return The Clazz
	 */
	public static FieldType createAnnotatedType(AnnotatedType annotatedType, Clazz source) {
		var clazz = create(annotatedType, source);
		return new FieldType(clazz, AnnoUtil.parseAnnotations(annotatedType), ReflectionUtil.getClassAnnotations(source));
	}

	public static Clazz createClass(Type bound, Clazz context) {
		if (bound == null) return null;
		return (Clazz) create(bound, context);
	}

	public static Clz create(Type bound, Clazz context) {
		return create(AnnoUtil.wrap(bound), context);
	}

	public static Clz create(AnnotatedType type, Clazz parent) {
		// if (type.getType() instanceof Class<?> clazz)
		// 	return CacheUtil.cache(CLASS_CACHE, clazz, (c) -> createFromType(type, parent));
		// else
		return createFromType(type, parent);
	}

	private static Clz createFromType(final AnnotatedType annotated, Clazz parent) {
		Clz clz = createRawFromType(annotated, parent).instantiate(annotated);
		if (parent == null) return clz;
		return clz.resolve(parent);
	}

	private static Clz createRawFromType(AnnotatedType annotated, Clazz parent) {
		final Type type = annotated.getType();
		if (type == null) return UNDEFINED;
		try {
			for (var entry : FORWARD_CLAZZERS) {
				if (entry.canProcess(type.getClass()))
					return entry.apply(annotated, parent);

			}
		} catch (ScanException e) {
			throw e.addParent(parent);
		}
		System.out.println("Hello There \n");
		throw new UnsupportedOperationException(type.getClass().getSimpleName() + " is unsupported");
	}

	/**
	 * Creates Clazz for every field of a clazz.
	 *
	 * @param clazz The Clazz to scan.
	 * @return The Clazz fields.
	 */
	@Deprecated
	public static FieldType[] scanFields(Clazz clazz) {
		return CacheUtil.cache(ALL_FIELD_CACHE, clazz, (c) -> {
			var fields = clazz.getFields();
			if (ReflectionUtil.getClassSuper(c) != null)
				return ArrayUtil.combine(scanFields(c.getSuper()), fields, FieldType[]::new);

			return fields;
		});
	}
}
