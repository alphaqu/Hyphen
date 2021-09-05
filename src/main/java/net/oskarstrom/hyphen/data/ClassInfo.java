package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.util.Color;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ClassInfo {
	private final Class<?> clazz;
	public final TypeMap typeMap;
	public String methodName;


	public ClassInfo(Class<?> clazz, TypeMap typeMap) {
		this.clazz = clazz;
		this.typeMap = typeMap;
	}

	public ClassInfo(Class<?> clazz) {
		this.clazz = clazz;
		this.typeMap = new TypeMap();
	}



	@Nullable
	public ClassInfo getSuperclass() {
		final Class<?> superclass = getClazz().getSuperclass();
		if (superclass != null) {
			return new ClassInfo(superclass, getClassTypeMap(superclass, getClazz().getGenericSuperclass()));
		}
		return null;
	}

	@Nullable
	public ClassInfo[] getInterfaces() {
		return getInterfacesInternal(0);
	}

	@Nullable
	private ClassInfo[] getInterfacesInternal(int padding) {
		final Class<?>[] interfaces = getClazz().getInterfaces();
		if (interfaces.length > 0) {
			ClassInfo[] interfacesInfo = new ClassInfo[interfaces.length + padding];
			final Type[] genericInterfaces = getClazz().getGenericInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				final Class<?> anInterface = interfaces[i];
				interfacesInfo[i + padding] = new ClassInfo(anInterface, getClassTypeMap(anInterface, genericInterfaces[i]));
			}
			return interfacesInfo;
		}
		return null;
	}

	@Nullable
	public ClassInfo[] getSuperAndInterfaces() {
		final @Nullable ClassInfo superclass = getSuperclass();
		final @Nullable ClassInfo[] interfaces = getInterfacesInternal(superclass == null ? 0 : 1);
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

	public List<FieldInfo> getFields() {
		List<FieldInfo> fieldInfos = new ArrayList<>();
		for (Field field : getClazz().getDeclaredFields()) {
			if (field.getDeclaredAnnotation(Serialize.class) != null) {
				fieldInfos.add(FieldInfo.create(this, field));
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
