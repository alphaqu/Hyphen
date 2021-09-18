package net.oskarstrom.hyphen.util;

import net.oskarstrom.hyphen.data.info.TypeInfo;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.LinkedHashMap;
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

	public static Type[] pathTo(Class<?> clazz, Class<?> targetParent, int depth) {
		if (clazz == targetParent)
			return new Type[depth];

		for (Type aClass : getInheritedType(clazz)) {
			Type[] classes = pathTo(castType(aClass), targetParent, depth + 1);
			if (classes != null) {
				classes[depth] = aClass;
				return classes;
			}
		}

		return null;
	}

	public static Type[] getInheritedType(Class<?> clazz) {
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


	//map all of the types,  A<String,Integer> -> B<K,S> == B<K = String, S = Integer>
	public static LinkedHashMap<String, TypeInfo> mapTypes(TypeInfo source, ParameterizedType type, @Nullable AnnotatedParameterizedType annotatedType) {
		var annotatedParameters = annotatedType == null ? null : annotatedType.getAnnotatedActualTypeArguments();
		var out = new LinkedHashMap<String, TypeInfo>();
		var clazz = (Class<?>) type.getRawType();
		var innerTypes = clazz.getTypeParameters();
		var parameters = type.getActualTypeArguments();
		for (int i = 0; i < parameters.length; i++) {
			AnnotatedType annotatedParameter = annotatedParameters == null ? null : annotatedParameters[i];
			out.put(innerTypes[i].getName(), TypeInfo.create(source, clazz, parameters[i], annotatedParameter));
		}
		return out;
	}

	public static LinkedHashMap<String, TypeInfo> mapSubclassTypes(TypeInfo superInfo, ParameterizedType subType) {
		var out = new LinkedHashMap<String, TypeInfo>();
		var superclass = (Class<?>) subType.getRawType();
		var actualTypeArguments = subType.getActualTypeArguments();
		var typeParameters = superclass.getTypeParameters();
		for (int j = 0; j < actualTypeArguments.length; j++) {
			out.put(actualTypeArguments[j].getTypeName(), TypeInfo.create(superInfo, superclass, typeParameters[j], null));
		}

		return out;
	}

	@SuppressWarnings("ConstantConditions")
	public static LinkedHashMap<String, TypeInfo> findTypes(TypeInfo source, Class<?> superType, Class<?> subClass, ParameterizedType type, AnnotatedParameterizedType annotatedType) {
		if (subClass == null) return null;
		else if (subClass == superType) {
			return mapTypes(source, type, annotatedType);
		} else {
			Type[] types = pathTo(subClass, superType, 0);

			if (types == null) {
				//TODO error
				throw new RuntimeException();
			}

			source = TypeInfo.create(source, superType, type, null);
			LinkedHashMap<String, TypeInfo> currentMapping = null;
			for (int i = types.length - 1; i >= 0; i--) {
				currentMapping = mapSubclassTypes(source, (ParameterizedType) types[i]);
			}

			LinkedHashMap<String, TypeInfo> out = new LinkedHashMap<>();
			for (var typeParameter : subClass.getTypeParameters()) {
				String typeName = typeParameter.getTypeName();
				out.put(typeName, currentMapping.get(typeName));
			}
			return out;
		}
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