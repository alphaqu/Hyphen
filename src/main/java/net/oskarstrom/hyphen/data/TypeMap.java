package net.oskarstrom.hyphen.data;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;

public class TypeMap extends LinkedHashMap<String, ClassInfo> {

	public TypeMap mapTypes(Class<?> clazz, ParameterizedType genericClassType) {
		var classTypeMap = new TypeMap();
		var classTypes = clazz.getTypeParameters();
		var oldTypes = genericClassType.getActualTypeArguments();

		//go through the types
		for (int i = 0; i < oldTypes.length; i++) {
			var internalTypeName = classTypes[i].getName();
			classTypeMap.put(internalTypeName, mapClass(oldTypes[i]));
		}

		return classTypeMap;
	}

	public ClassInfo getFieldClass(Field field) {
		return mapClass(field.getGenericType());
	}

	private ClassInfo mapClass(Type oldType) {
		//check if this has a static class assigned to it (Field<Integer>)
		if (oldType instanceof Class<?> classType)
			return new ClassInfo(classType);

		//if this has a static class and a parameter, we want to scan it again for its inner types
		if (oldType instanceof ParameterizedType type) {
			final Class<?> rawType = (Class<?>) type.getRawType();
			return new ClassInfo(rawType, mapTypes(rawType, type));
		}
		//check if the type name exists (public class Holder<K> / Field<K>)
		var aClass = get(oldType.getTypeName());
		if (aClass == null) {
			throw new RuntimeException();
		}
		return aClass;
	}
}
