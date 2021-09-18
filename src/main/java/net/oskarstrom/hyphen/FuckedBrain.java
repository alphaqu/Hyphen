package net.oskarstrom.hyphen;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;

public class FuckedBrain {


	public static void main(String[] args) {
		var currentMapping = new LinkedHashMap<String, Class<?>>();
		currentMapping.put("A", Integer.class);



		Class<C3> c2Class = C3.class;

		Type[] types = pathTo(c2Class, C1.class, 0);


		for (int i = types.length - 1; i >= 0; i--) {
			currentMapping = mapInherent(types[i],currentMapping);
		}

		System.out.println(currentMapping);
	}

	public static LinkedHashMap<String, Class<?>> mapInherent(Type generic, LinkedHashMap<String, Class<?>> input) {
		var out = new LinkedHashMap<String, Class<?>>();
		if (generic instanceof ParameterizedType type) {
			var superclass = (Class<?>) type.getRawType();
			var actualTypeArguments = type.getActualTypeArguments();
			var typeParameters = superclass.getTypeParameters();
			for (int j = 0; j < actualTypeArguments.length; j++) {
				out.put(actualTypeArguments[j].getTypeName(), input.get(typeParameters[j].getTypeName()));
			}
		}
		return out;
	}


	public static Type[] pathTo(Class<?> clazz, Class<?> targetParent, int depth) {
		if (clazz == targetParent)
			return new Type[depth];

		for (Type aClass : splitGeneric(clazz)) {
			Type[] classes = pathTo(getClazz(aClass), targetParent, depth + 1);
			if (classes != null) {
				classes[depth] = aClass;
				return classes;
			}
		}

		return null;
	}

	private static Class<?> getClazz(Type type) {
		if (type instanceof ParameterizedType parameterizedType) {
			return (Class<?>) parameterizedType.getRawType();
		}
		return (Class<?>) type;
	}


	public static Type[] splitGeneric(Class<?> clazz) {
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


	public static class C1<A> {

	}

	public static class C2<B> extends C1<String> {

	}

	public static class C3<C> extends C2<C> {

	}

	public record TypeClassEntry(Type type, Class<?> clazz) {
	}
}
