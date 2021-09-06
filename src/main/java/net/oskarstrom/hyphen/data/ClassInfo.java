package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.DebugHandler;
import net.oskarstrom.hyphen.annotation.Serialize;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ClassInfo {
	public final TypeMap typeMap;
	public final Class<?> clazz;
	@Nullable
	public final Type genericType;
	public String methodName;


	protected ClassInfo(Class<?> clazz, TypeMap typeMap, @Nullable Type genericType) {
		this.clazz = clazz;
		this.typeMap = typeMap;
		this.genericType = genericType;
	}

	protected ClassInfo(Class<?> clazz, @Nullable Type genericType) {
		this.clazz = clazz;
		this.genericType = genericType;
		this.typeMap = new TypeMap();
	}


	public static ClassInfo create(@Nullable ClassInfo parent, Class<?> clazz, @Nullable Type genericType, boolean mapTypes) {
		if (mapTypes && genericType != null && parent != null) {
			return new ClassInfo(clazz, parent.getClassTypeMap(clazz, genericType), genericType);
		}
		return new ClassInfo(clazz, genericType);
	}

	public static ClassInfo create(@Nullable ClassInfo parent, Class<?> clazz, @Nullable Type genericType) {
		return create(parent, clazz, genericType, true);
	}

	public static ClassInfo create(Class<?> clazz, TypeMap typeMap, @Nullable Type genericType) {
		return new ClassInfo(clazz, typeMap, genericType);
	}

	@Nullable
	public ClassInfo getSuperclass(boolean mapTypes) {
		Class<?> thisClazz = getClazz();
		final Class<?> superclass = thisClazz.getSuperclass();
		if (superclass != null) {
			return ClassInfo.create(this, superclass, thisClazz.getGenericSuperclass(), mapTypes);
		}
		return null;
	}

	@Nullable
	public ClassInfo[] getInterfaces() {
		return getInterfacesInternal(0, true);
	}

	@Nullable
	private ClassInfo[] getInterfacesInternal(int padding, boolean mapTypes) {
		final Class<?>[] interfaces = getClazz().getInterfaces();
		if (interfaces.length > 0) {
			ClassInfo[] interfacesInfo = new ClassInfo[interfaces.length + padding];
			final Type[] genericInterfaces = getClazz().getGenericInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				interfacesInfo[i + padding] = ClassInfo.create(this, interfaces[i], genericInterfaces[i], mapTypes);
			}
			return interfacesInfo;
		}
		return null;
	}

	@Nullable
	public ClassInfo[] getSuperAndInterfaces(boolean mapTypes) {
		final @Nullable ClassInfo superclass = getSuperclass(mapTypes);
		final @Nullable ClassInfo[] interfaces = getInterfacesInternal(superclass == null ? 0 : 1, mapTypes);
		if (interfaces == null) {
			if (superclass == null) {
				return null;
			}
			return new ClassInfo[]{superclass};
		}
		if (superclass != null) {
			interfaces[0] = superclass;
		}
		return interfaces;
	}

	public List<FieldInfo> getFields(SubclassMap subclasses) {
		List<FieldInfo> fieldInfos = new ArrayList<>();
		for (Field field : getClazz().getDeclaredFields()) {
			if (field.getDeclaredAnnotation(Serialize.class) != null) {
				fieldInfos.add(FieldInfo.create(this, field, subclasses));
			}
		}
		return fieldInfos;
	}

	public ClassInfo getFieldClass(Field field) {
		return typeMap.getFieldClass(field);
	}

	public TypeMap getClassTypeMap(Class<?> clazz, Type genericClassType) {
		TypeMap out;
		if (genericClassType instanceof ParameterizedType type) {
			//if it has parameters map it from its source's parameters
			out = typeMap.mapTypes(clazz, type);
		} else {
			//otherwise, just make a new typeMap to prevent NPE
			out = new TypeMap();
		}
		return out;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public TypeMap getTypes() {
		return typeMap;
	}

	@Override
	public String toString() {
		return clazz.getSimpleName();
	}

	public String parseMethodName() {
		StringBuilder stringBuilder = new StringBuilder();
		getMethodName(stringBuilder);
		return stringBuilder.toString();
	}

	private void getMethodName(StringBuilder builder) {
		builder.append(clazz.getSimpleName());
		if (typeMap != null && typeMap.entrySet().size() > 0) {
			builder.append('<');
			for (Iterator<ClassInfo> iterator = typeMap.values().iterator(); iterator.hasNext(); ) {
				ClassInfo clazz = iterator.next();
				clazz.getMethodName(builder);
				if (iterator.hasNext()) {
					builder.append('$');
				}
			}
			builder.append('>');
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClassInfo classInfo = (ClassInfo) o;
		return Objects.equals(clazz, classInfo.clazz) && Objects.equals(typeMap, classInfo.typeMap);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clazz, typeMap);
	}
}
