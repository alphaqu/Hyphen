package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.info.ParameterizedClassInfo;
import dev.quantumfusion.hyphen.info.SubclassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.thr.ThrowEntry;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.HyphenException;
import dev.quantumfusion.hyphen.thr.exception.IllegalInheritanceException;
import dev.quantumfusion.hyphen.thr.exception.IncompatibleTypeException;
import dev.quantumfusion.hyphen.thr.exception.NotYetImplementedException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static dev.quantumfusion.hyphen.ScanHandler.UNKNOWN_INFO;

public class TypeUtil {
	//map all of the types,  A<String,Integer> -> B<K,S> == B<K = String, S = Integer>
	public static LinkedHashMap<String, TypeInfo> mapTypes(TypeInfo source, ParameterizedType type, @Nullable AnnotatedParameterizedType annotatedType) {
		var annotatedParameters = annotatedType == null ? null : annotatedType.getAnnotatedActualTypeArguments();
		var out = new LinkedHashMap<String, TypeInfo>();
		var clazz = (Class<?>) type.getRawType();
		var innerTypes = clazz.getTypeParameters();
		var parameters = type.getActualTypeArguments();
		for (int i = 0; i < parameters.length; i++) {
			AnnotatedType annotatedParameter = annotatedParameters == null ? null : annotatedParameters[i];
			out.put(innerTypes[i].getName(), ScanHandler.create(source, clazz, parameters[i], annotatedParameter));
		}
		return out;
	}

	public static Map<String, TypeInfo> mapSubclassTypes(Map<String, TypeInfo> typeInfos, ParameterizedType superType) {
		try {
			var out = new HashMap<String, TypeInfo>();
			var superclass = ScanUtils.getClazz(superType);
			var actualTypeArguments = superType.getActualTypeArguments();
			var typeParameters = superclass.getTypeParameters();

			assert actualTypeArguments.length == typeParameters.length;

			for (int i = 0; i < actualTypeArguments.length; i++) {
				unifyType(out, typeInfos.getOrDefault(typeParameters[i].getName(), UNKNOWN_INFO), actualTypeArguments[i]);
			}

			return out;
		} catch (HyphenException ex) {
			throw ex.addEntries(
					ThrowEntry.of("Current super type declaration", superType),
					ThrowEntry.of("Super type", ScanUtils.getClazz(superType).toGenericString()),
					ThrowEntry.of("Super type values", typeInfos));
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	public static void unifyType(Map<? super String, ? super TypeInfo> resolved, TypeInfo typeInfo, Type typeParameter) {
		try {
			if (typeParameter instanceof Class<?> clazz) {
				if (typeInfo == UNKNOWN_INFO) {
					// resolve type that was unknown
					// I don't think we have to do something here? Although I do think there might be invalid case that we
					// need to consider
				} else if (typeInfo.clazz != clazz) {
					// TODO: error not good enough, should handles cases like IncompatibleTypeFail
					throw ThrowHandler.fatal(IncompatibleTypeException::new, "Not a valid subtype");
				}
			} else if (typeParameter instanceof TypeVariable<?> typeVariable) {
				if (resolved.containsKey(typeVariable.getName())) {
					// check if it's the same
					if (!resolved.getOrDefault(typeVariable.getName(), UNKNOWN_INFO).equals(typeInfo)) {
						throw ThrowHandler.fatal(IncompatibleTypeException::new, "Invalid type unification",
								ThrowEntry.of("Parameter", typeVariable.getName()),
								ThrowEntry.of("Previously discovered type", resolved.get(typeVariable.getName()))
						);
					}
				} else {
					// TODO: check bounds
					resolved.put(typeVariable.getName(), typeInfo);

					Type[] bounds = typeVariable.getBounds();

					for (Type bound : bounds) {
						if (bound == Object.class) {
							// TODO: i think this shouldn't be a special case here and instead be handled by class/class unifying?
							continue;
						}

						// eg Foo<A, B extends List<A>> extends Bar<B> with Bar<List<Int>>
						// TODO: we should mark this as a <? extends Bound>
						unifyType(resolved, typeInfo, bound);
					}
				}
			} else if (typeParameter instanceof ParameterizedType superParameterizedType) {
				if (typeInfo instanceof ParameterizedClassInfo selfParameterizedType) {
					Class<?> clazz = ScanUtils.getClazz(superParameterizedType);
					if (clazz == typeInfo.clazz) {
						var superTypeArguments = superParameterizedType.getActualTypeArguments();
						var selfTypeArguments = selfParameterizedType.types;

						assert superTypeArguments.length == selfTypeArguments.size();

						int i = 0;
						for (var selfType : selfTypeArguments.values()) {
							Type superType = superTypeArguments[i++];

							unifyType(resolved, selfType, superType);
						}
					} else if (clazz.isAssignableFrom(typeInfo.clazz)) {
						throw ThrowHandler.fatal(
								NotYetImplementedException::new, "parameterized type unification through supertypes",
								ThrowEntry.of("TypeParameter", typeParameter)
						);
					} else throw ThrowHandler.fatal(IncompatibleTypeException::new, "Incompatible Types");
				} else if (typeInfo instanceof SubclassInfo)
					throw ThrowHandler.fatal(NotYetImplementedException::new, "NYI: Polymorphic type unification");
				else throw ThrowHandler.fatal(IllegalArgumentException::new, "Unexpected type unification request");
			} else throw ThrowHandler.fatal(IllegalArgumentException::new, "Unexpected type unification request");
		} catch (HyphenException ex) {
			throw ex.addEntries(
					ThrowEntry.of("Resolved", resolved),
					ThrowEntry.of("TypeInfo", typeInfo),
					ThrowEntry.of("TypeParameter", typeParameter)
			);
		}
	}

	public static LinkedHashMap<String, TypeInfo> findTypes(TypeInfo source, Class<?> superClass, Class<?> subClass, ParameterizedType supperType, AnnotatedParameterizedType annotatedSuperType) {
		try {
			if (subClass == null) return null;
			else if (subClass == superClass) {
				return mapTypes(source, supperType, annotatedSuperType);
			} else {
				Type[] types = ScanUtils.pathTo(subClass, superClass, 0);

				if (types == null) {
					throw ThrowHandler.fatal(
							IllegalInheritanceException::new, "Subclass does not implement super.");
				}

				Map<String, TypeInfo> typeMap = mapTypes(source, supperType, annotatedSuperType);

				for (int i = types.length - 1; i >= 0; i--) {
					Type currentType = types[i];
					typeMap = mapSubclassTypes(typeMap, (ParameterizedType) currentType);
				}

				LinkedHashMap<String, TypeInfo> out = new LinkedHashMap<>();
				for (var typeParameter : subClass.getTypeParameters()) {
					String typeName = typeParameter.getTypeName();
					TypeInfo value = typeMap.getOrDefault(typeName, UNKNOWN_INFO);

					out.put(typeName, value);
				}
				return out;
			}
		} catch (HyphenException ex) {
			throw ex.addEntries(
					ThrowEntry.of("Source", source),
					ThrowEntry.of("FieldClass", superClass.getSimpleName()),
					ThrowEntry.of("FieldType", supperType),
					ThrowEntry.of("AnnotatedFieldType", annotatedSuperType),
					ThrowEntry.of("SubClass", subClass.getSimpleName())
			);
		}
	}

	public static Type[] getInheritedTypes(Class<?> clazz) {
		Type superclass = clazz.getGenericSuperclass();
		Type[] interfaces = clazz.getGenericInterfaces();

		if (superclass == null) {
			return interfaces;
		} else {
			Type[] out = new Type[interfaces.length + 1];
			out[0] = superclass;
			System.arraycopy(interfaces, 0, out, 1, interfaces.length);
			return out;
		}
	}
}
