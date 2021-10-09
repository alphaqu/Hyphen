package dev.quantumfusion.hyphen.scan.type;

import java.lang.reflect.AnnotatedType;
import java.util.Map;

/**
 * Represents a Type
 */
@SuppressWarnings("ClassReferencesSubclass")
public interface Clz {

	/**
	 * Resolves this class, and its references to type parameters in the context of the given class
	 *
	 * @param context The class to resolve the type parameters on
	 * @return A Clz (of the same type as this) that represents this type in the given context
	 */
	Clz resolve(Clazz context);

	/**
	 * Instantiates this class, and its type parameters to match the given annotated type
	 *
	 * @param annotatedType The typ to match
	 * @return The instantiated type
	 */
	default Clz instantiate(AnnotatedType annotatedType) {
		return this;
	}

	default void finish(AnnotatedType type, Clazz source) {
	}

	Clz merge(Clz other, Map<TypeClazz, TypeClazz> types);
}
