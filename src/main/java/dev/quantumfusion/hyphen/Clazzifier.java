package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.thr.ScanException;
import dev.quantumfusion.hyphen.type.*;
import dev.quantumfusion.hyphen.util.AnnoUtil;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.CacheUtil;
import dev.quantumfusion.hyphen.util.ReflectionUtil;

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
	private static final List<ClzCreator> FORWARD_CLAZZERS = new ArrayList<>();
	private static final Map<Class<?>, Clz> CLASS_CACHE = new IdentityHashMap<>();
	private static final Map<Clazz, AnnType[]> ALL_FIELD_CACHE = new IdentityHashMap<>();

	static {
		FORWARD_CLAZZERS.add(ClzCreator
				.of(ParameterizedType.class, ParameterizedClazz::createParameterizedClass)
				.cachedOrPostProcess(Clazz::getClassFrom, ParameterizedClazz::finish));
		FORWARD_CLAZZERS.add(ClzCreator.of(TypeVariable.class, (type, clazz) -> TypeClazz.createRaw((TypeVariable<?>) type.getType())));
		FORWARD_CLAZZERS.add(ClzCreator
				.of(GenericArrayType.class, (annotatedType, source) -> ArrayClazz.createArray())
				.postProcess(Clz::finish)
		);
		FORWARD_CLAZZERS.add(ClzCreator.of(WildcardType.class, (type, clazz) -> UNDEFINED));
		FORWARD_CLAZZERS.add(ClzCreator
				.of(Class.class, (type, source) -> Clazz.createRawClazz(type))
				.cachedOrPostProcess(Clazz::getClassFrom, Clz::finish)
		);
	}


/**
	 * This creates a {@link Clazz} from an AnnotatedType.<br> This method is cached and the actual implementations is in the {@link Clazzifier#createFromType}
	 *
	 * @param type   An {@link AnnotatedType} that you want to create a {@link Clazz} from
	 * @param parent The source of the AnnotatedType. Used for mapping Class Parameters.
	 * @return The Clazz
	 */
	public static AnnType createAnnotatedType(AnnotatedType annotatedType, Clazz source) {
		var clazz = create(annotatedType, null);
		return new AnnType(clazz, AnnoUtil.parseAnnotations(annotatedType), ReflectionUtil.getClassAnnotations(source));
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

	private static Clz createFromType(AnnotatedType annotated, Clazz parent) {
		System.out.println(annotated);
		System.out.println(parent);
		System.out.println();
		Clz clz = createRawFromType(annotated, parent).instantiate(annotated);
		if (parent == null)
			return clz;
		return clz.resolve(parent);
	}

	private static Clz createRawFromType(AnnotatedType annotated, Clazz parent) {
		final Type type = annotated.getType();
		try {
			for (var entry : FORWARD_CLAZZERS) {
				if (entry.canProcess(type.getClass())) {
					return entry.apply(annotated, parent);
				}
			}
		} catch (ScanException e) {
			e.parents.add(parent);
			throw e;
		}
		throw new UnsupportedOperationException(type.getClass().getSimpleName() + " is unsupported");
	}

	/**
	 * Creates Clazz for every field of a clazz.
	 *
	 * @param clazz The Clazz to scan.
	 * @return The Clazz fields.
	 */
	public static AnnType[] scanFields(Clazz clazz) {
		return CacheUtil.cache(ALL_FIELD_CACHE, clazz, (c) -> {
			final AnnType[] fields = clazz.getFields();
			final AnnotatedType aSuper = ReflectionUtil.getClassSuper(c);
			if (aSuper != null)
				return ArrayUtil.combine(scanFields(c.getSuper()), fields, AnnType[]::new);

			return fields;
		});
	}
}
