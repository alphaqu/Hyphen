package net.oskarstrom.hyphen.util;

import net.oskarstrom.hyphen.SerializerFactory;
import net.oskarstrom.hyphen.data.info.ClassInfo;
import net.oskarstrom.hyphen.data.info.TypeInfo;
import net.oskarstrom.hyphen.thr.ThrowHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class ScanUtils {

	public static Class<?>[] pathTo(Class<?> clazz, Predicate<? super Class<?>> matcher, Function<Class<?>, Class<?>[]> splitter, int depth) {
		if (matcher.test(clazz))
			return new Class[depth];

		for (Class<?> aClass : splitter.apply(clazz)) {
			Class<?>[] classes = pathTo(aClass, matcher, splitter, depth + 1);
			if (classes != null) {
				classes[depth] = aClass;
				return classes;
			}
		}

		return null;
	}

	//map all of the types,  A<String,Integer> -> B<K,S> == B<K = String, S = Integer>
	public static LinkedHashMap<String, TypeInfo> mapTypes(ClassInfo source, ParameterizedType type, AnnotatedParameterizedType annotatedType) {
		var out = new LinkedHashMap<String, TypeInfo>();
		var clazz = (Class<?>) type.getRawType();
		var innerTypes = clazz.getTypeParameters();
		var annotatedParameters = annotatedType.getAnnotatedActualTypeArguments();
		var parameters = type.getActualTypeArguments();
		for (int i = 0; i < parameters.length; i++) {
			out.put(innerTypes[i].getName(), TypeInfo.create(source, clazz, parameters[i], annotatedParameters[i]));
		}
		return out;
	}

	public static TypeInfo mapType( ClassInfo source, AnnotatedType annotatedType) {
		return TypeInfo.create(source, castType(annotatedType.getType()), annotatedType.getType(), annotatedType);
	}

	public static Map<String, AnnotatedType> findTypes(Class<?> subType, Class<?> poly, ParameterizedType genericPoly, AnnotatedParameterizedType annotatedGenericPoly) {
		if (subType == null) {
			return null;
		} else if (subType == poly) {
			TypeVariable<? extends Class<?>>[] typeParameters = subType.getTypeParameters();
			AnnotatedType[] actualTypeArguments = annotatedGenericPoly.getAnnotatedActualTypeArguments();

			assert typeParameters.length == actualTypeArguments.length;

			Map<String, AnnotatedType> types = new HashMap<>(typeParameters.length);

			for (int i = 0; i < typeParameters.length; i++) {
				types.put(typeParameters[i].getName(), actualTypeArguments[i]);
			}

			return types;
		} else {
			Type superclass = subType.getGenericSuperclass();
			var types = findTypes(subType.getSuperclass(), poly, genericPoly, annotatedGenericPoly);
			if (types == null) {
				Type[] genericInterfaces = subType.getGenericInterfaces();
				Class<?>[] interfaces = subType.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					types = findTypes(interfaces[i], poly, genericPoly, annotatedGenericPoly);
					if (types != null) {
						superclass = genericInterfaces[i];
						break;
					}
				}
			}

			if (types == null) {
				return null;
			}


			if (superclass instanceof ParameterizedType parameterizedType
			) {
				Type[] superTypeArguments = parameterizedType.getActualTypeArguments();
				TypeVariable<?>[] typeParameters = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();

				assert superTypeArguments.length == typeParameters.length;

				var map = new HashMap<String, AnnotatedType>();

				for (int i = 0; i < superTypeArguments.length; i++) {
					resolveType(types, map, superTypeArguments[i], types.get(typeParameters[i].getName()));
				}

				for (TypeVariable<? extends Class<?>> typeParameter : subType.getTypeParameters()) {
					if (!map.containsKey(typeParameter.getName())) {
						map.put(typeParameter.getName(), SerializerFactory.UNKNOWN.UNKNOWN);
					}
				}


				return map;
			}
			throw new IllegalStateException("Not yet Implemented");

		}
	}

	public static void resolveType(
			Map<String, AnnotatedType> lookup,
			Map<String, AnnotatedType> resolved,
			Type superTypeArgument,
			AnnotatedType type) {

		if (superTypeArgument instanceof Class<?> clazz) {

			if (type.getType() == clazz) {
				// all is fine
			} else if (type.getType() == SerializerFactory.UNKNOWN.UNKNOWN) {
				// resolve type that was unknown
				// I don't think we have to do something here? Although I do think there might be invalid case that we
				// need to consider
			} else {
				// TODO: handle `? extends` and `? super`
				throw new IllegalArgumentException("Could not unify types, " + type + " with " + type.getType() + " and " + clazz.getName());
			}
		} else if (superTypeArgument instanceof TypeVariable<?> typeVariable) {
			if (resolved.containsKey(typeVariable.getName())) {
				// check if it's the same
				if (resolved.get(typeVariable.getName()).equals(type)) {
					// all is fine
				} else {
					throw ThrowHandler.fatal(IllegalArgumentException::new, "Invalid type unification",
							ThrowHandler.ThrowEntry.of("Lookup", lookup),
							ThrowHandler.ThrowEntry.of("Resolved", resolved),
							ThrowHandler.ThrowEntry.of("SuperType", superTypeArgument),
							ThrowHandler.ThrowEntry.of("Type", type),
							ThrowHandler.ThrowEntry.of("Previously discovered type", resolved.get(typeVariable.getName()))
					);
				}
			} else {
				// TODO: check bounds?
				resolved.put(typeVariable.getName(), type);

				Type[] bounds = typeVariable.getBounds();

				for (Type bound : bounds) {
					if (bound == Object.class) {
						// TODO: i think this shouldn't be a special case here and instead be handled by class/class unifying?
						continue;
					}

					// eg Foo<A, B extends List<A>> extends Bar<B> with Bar<List<Int>>
					resolveType(lookup, resolved, bound, type);
				}

			}
		} else if (superTypeArgument instanceof ParameterizedType superParameterizedType &&
				type.getType() instanceof ParameterizedType selfParameterizedType &&
				type instanceof AnnotatedParameterizedType selfAnnotatedParameterizedType
		) {
			if (superParameterizedType.getRawType().equals(selfParameterizedType.getRawType())) {
				Type[] superTypeArguments = superParameterizedType.getActualTypeArguments();
				AnnotatedType[] selfTypeArguments = selfAnnotatedParameterizedType.getAnnotatedActualTypeArguments();

				assert superTypeArguments.length == selfTypeArguments.length;

				for (int i = 0; i < superTypeArguments.length; i++) {
					AnnotatedType selfType = selfTypeArguments[i];
					Type superType = superTypeArguments[i];

					resolveType(lookup, resolved, superType, selfType);
				}
			} else {
				throw ThrowHandler.fatal(IllegalStateException::new, "NYI: parameterized type unification through supertypes",
						ThrowHandler.ThrowEntry.of("Lookup", lookup),
						ThrowHandler.ThrowEntry.of("Resolved", resolved),
						ThrowHandler.ThrowEntry.of("SuperType", superTypeArgument),
						ThrowHandler.ThrowEntry.of("Type", type),
						ThrowHandler.ThrowEntry.of("TypeType", type.getType())
				);
			}
		} else {
			throw ThrowHandler.fatal(IllegalArgumentException::new, "Unexpected type unification request",
					ThrowHandler.ThrowEntry.of("Lookup", lookup),
					ThrowHandler.ThrowEntry.of("Resolved", resolved),
					ThrowHandler.ThrowEntry.of("SuperType", superTypeArgument),
					ThrowHandler.ThrowEntry.of("Type", type)
			);
		}
	}

	@NotNull
	public static LinkedHashMap<String, TypeInfo> mapAllTypes(
			ClassInfo source,
			TypeVariable<? extends Class<?>>[] typeParameters,
			Map<String, ? extends AnnotatedType> types) {

		LinkedHashMap<String, TypeInfo> typeInfoMap = new LinkedHashMap<>(typeParameters.length);

		for (TypeVariable<? extends Class<?>> typeParameter : typeParameters) {
			AnnotatedType annotatedType = types.get(typeParameter.getName());

			if (annotatedType == null || annotatedType == SerializerFactory.UNKNOWN.UNKNOWN) {
				throw ThrowHandler.fatal(IllegalStateException::new, "Did not find type",
						ThrowHandler.ThrowEntry.of("TypeName", typeParameter.getName())
				);
			}

			typeInfoMap.put(typeParameter.getName(), ScanUtils.mapType(source, annotatedType));
		}

		return typeInfoMap;
	}

	public static Class<?> castType(Type type) {
		if (type instanceof Class<?> c) {
			return c;
		} else if (type instanceof ParameterizedType parameterizedType) {
			return castType(parameterizedType.getRawType());
		} else {
			throw new IllegalStateException("Blame alpha: " + type.getClass().getSimpleName() + ": " + type.getTypeName());
		}
	}
}