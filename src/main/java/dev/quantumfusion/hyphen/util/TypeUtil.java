package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.info.ParameterizedInfo;
import dev.quantumfusion.hyphen.info.SubclassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.info.WildcardInfo;
import dev.quantumfusion.hyphen.thr.ThrowEntry;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.HyphenException;
import dev.quantumfusion.hyphen.thr.exception.IllegalInheritanceException;
import dev.quantumfusion.hyphen.thr.exception.IncompatibleTypeException;
import dev.quantumfusion.hyphen.thr.exception.NotYetImplementedException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static dev.quantumfusion.hyphen.ScanHandler.UNKNOWN_INFO;

public class TypeUtil {
	public static Type applyType(Type type, AnnotatedType annotatedType) {
		if (type instanceof Class<?> clazz) {
			if (clazz.isArray()) return new ArrayType.ArrayTypeImpl(clazz.getComponentType(), clazz);
		}
		return type;
	}


	//map all of the types,  A<S, K>{ B<K,S> } => A<String,Integer> -> B<K,S> == B<String, Integer>
	public static LinkedHashMap<String, TypeInfo> mapTypes(ScanHandler factory, TypeInfo source, ParameterizedType type, @Nullable AnnotatedParameterizedType annotatedType) {
		var annotatedParameters = annotatedType == null ? null : annotatedType.getAnnotatedActualTypeArguments();
		var out = new LinkedHashMap<String, TypeInfo>();
		var clazz = ScanUtils.getClazz(type.getRawType());
		var innerTypes = clazz.getTypeParameters();
		var parameters = type.getActualTypeArguments();
		for (int i = 0; i < parameters.length; i++) {
			AnnotatedType annotatedParameter = annotatedParameters == null ? null : annotatedParameters[i];
			// TODO: fix
			out.put(innerTypes[i].getName(), factory.create(source, null, parameters[i], annotatedParameter));
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
				unifyType(out, typeInfos.getOrDefault(typeParameters[i].getName(), UNKNOWN_INFO), actualTypeArguments[i], UnificationType.EXTENDS);
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
	public static void unifyType(Map<? super String, ? super TypeInfo> resolved, TypeInfo typeInfo, Type typeParameter, UnificationType unificationType) {
		try {
			if (typeParameter instanceof Class<?> clazz) {
				if (typeInfo == UNKNOWN_INFO) {
					// resolve type that was unknown
					// I don't think we have to do something here? Although I do think there might be invalid case that we
					// need to consider
				} else if (!unificationType.canAssign2(typeInfo, clazz)) {
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
					System.out.println(Arrays.toString(bounds));

					for (Type bound : bounds) {
						// eg Foo<A, B extends List<A>> extends Bar<B> with Bar<List<Int>>
						// TODO: we should mark this as a <? extends Bound>
						// TODO: if the input kind is super, what should this be? is that even a possibility?
						unifyType(resolved, typeInfo, bound, UnificationType.EXTENDS);
					}
				}
			} else if (typeParameter instanceof ParameterizedType superParameterizedType) {
				if (typeInfo instanceof ParameterizedInfo selfParameterizedType) {
					Class<?> clazz = ScanUtils.getClazz(superParameterizedType);
					if (clazz == typeInfo.getClazz()) {
						var superTypeArguments = superParameterizedType.getActualTypeArguments();
						var selfTypeArguments = selfParameterizedType.types;

						assert superTypeArguments.length == selfTypeArguments.size();

						int i = 0;
						for (var selfType : selfTypeArguments.values()) {
							Type superType = superTypeArguments[i++];

							unifyType(resolved, selfType, superType, UnificationType.EXACT);
						}
					} else if (clazz.isAssignableFrom(typeInfo.getClazz())) {
						throw ThrowHandler.fatal(
								NotYetImplementedException::new, "parameterized type unification through supertypes",
								ThrowEntry.of("TypeParameter", typeParameter)
						);
					} else throw ThrowHandler.fatal(IncompatibleTypeException::new, "Incompatible Types");
				} else if (typeInfo instanceof SubclassInfo)
					throw ThrowHandler.fatal(NotYetImplementedException::new, "NYI: Parametric <> Polymorphic type unification");
				else if (typeInfo instanceof WildcardInfo)
					throw ThrowHandler.fatal(NotYetImplementedException::new, "NYI: Parametric <> Wildcard type unification");
				else throw ThrowHandler.fatal(IllegalArgumentException::new, "Unexpected type unification request");
			} else if (typeParameter instanceof WildcardType wildcardType) {
				for (Type upperBound : wildcardType.getUpperBounds()) {
					// TODO: can we just pass extends here?
					unifyType(resolved, typeInfo, upperBound, UnificationType.EXTENDS);
				}
				for (Type lowerBound : wildcardType.getLowerBounds()) {
					// TODO: can we just pass extends here?
					unifyType(resolved, typeInfo, lowerBound, UnificationType.SUPER);
				}
			} else {
				throw ThrowHandler.fatal(IllegalArgumentException::new, "Unexpected type unification request");
			}
		} catch (HyphenException ex) {
			throw ex.addEntries(
					ThrowEntry.of("Resolved", resolved),
					ThrowEntry.of("TypeInfo", typeInfo),
					ThrowEntry.of("TypeParameter", typeParameter)
			);
		}
	}

	public static LinkedHashMap<String, TypeInfo> findTypes(
			ScanHandler factory,
			TypeInfo source,
			Class<?> superClass,
			ParameterizedType superType,
			AnnotatedParameterizedType annotatedSuperType,
			Class<?> subClass
	) {
		try {
			if (subClass == null) return null;
			else if (subClass == superClass) {
				return mapTypes(factory, source, superType, annotatedSuperType);
			} else {
				Type[] types = ScanUtils.pathTo(subClass, superClass, 0);

				if (types == null) {
					throw ThrowHandler.fatal(
							IllegalInheritanceException::new, "Subclass does not implement super.");
				}

				Map<String, TypeInfo> typeMap = mapTypes(factory, source, superType, annotatedSuperType);

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
					ThrowEntry.of("SuperClass", superClass),
					ThrowEntry.of("SuperType", superType),
					ThrowEntry.of("AnnotatedSuperType", annotatedSuperType),
					ThrowEntry.of("SubClass", subClass)
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

	public static Class<?>[] getInheritedClasses(Class<?> clazz) {
		Class<?> superclass = clazz.getSuperclass();
		Class<?>[] interfaces = clazz.getInterfaces();

		if (superclass == null) {
			return interfaces;
		} else {
			Class<?>[] out = new Class<?>[interfaces.length + 1];
			out[0] = superclass;
			System.arraycopy(interfaces, 0, out, 1, interfaces.length);
			return out;
		}
	}

	enum UnificationType {
		EXACT {
			@Override
			boolean canAssign(Class<?> a, Class<?> b) {
				return a == b;
			}
		}, EXTENDS {
			@Override
			boolean canAssign(Class<?> a, Class<?> b) {
				return b.isAssignableFrom(a);
			}
		}, SUPER {
			@Override
			boolean canAssign(Class<?> a, Class<?> b) {
				return a.isAssignableFrom(b);
			}
		};

		abstract boolean canAssign(Class<?> a, Class<?> b);

		public boolean canAssign2(TypeInfo typeInfo, Class<?> clazz) {
			// TODO: consider making this a method on typeinfo instead?
			if (typeInfo instanceof WildcardInfo wildcardInfo) {
				// TODO: fix
				return true;
			} else if (typeInfo instanceof SubclassInfo subclassInfo) {
				for (TypeInfo subInfo : subclassInfo.classInfos) if (this.canAssign2(subInfo, clazz)) return true;
				return false;
			} else {
				return this.canAssign(typeInfo.getClazz(), clazz);
			}
		}
	}

}
