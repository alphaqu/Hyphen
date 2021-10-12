package dev.quantumfusion.hyphen.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;

public class ScanUtil {
	public static Type[] findPath(Type root, Predicate<Type> matcher, Function<Type, Type[]> splitter) {
		var queue = new ArrayDeque<Type[]>();
		var explored = new HashSet<Type>();

		// Add start entry
		explored.add(root);
		queue.add(new Type[]{root});
		while (!queue.isEmpty()) {
			var parentPath = queue.poll();
			var parentPathLength = parentPath.length;

			// last entry is always the parent
			var parent = parentPath[parentPathLength - 1];

			// check if the current class is matching and return its path
			if (matcher.test(parent)) return parentPath;

			// iterate through children
			for (Type child : splitter.apply(parent)) {
				//if its already explored skip
				if (explored.contains(child)) continue;
				explored.add(child);

				// Use parents path and add itself to the last entry
				queue.add(append(parentPath, child));
			}
		}

		return null;
	}

	public static Type[] getInherited(Type type) {
		var clazz = getClassFrom(type);
		var classInterface = clazz.getGenericInterfaces();
		var classSuper = clazz.getGenericSuperclass();
		if (classSuper != null) return append(classInterface, classSuper);
		return classInterface;
	}

	public static Type[] append(Type[] oldArray, Type value) {
		final int length = oldArray.length;
		Type[] out = new Type[length + 1];
		System.arraycopy(oldArray, 0, out, 0, length);
		out[length] = value;
		return out;
	}

	public static Type wrap(Class<?> clazz) {
		return new ParameterizedType() {
			@Override
			public Type[] getActualTypeArguments() {
				return clazz.getTypeParameters();
			}

			@Override
			public Type getRawType() {
				return clazz;
			}

			@Override
			public Type getOwnerType() {
				return null;
			}
		};
	}

	public static Class<?> getClassFrom(Type type) {
		if (type instanceof Class<?> c) return c;
		if (type instanceof ParameterizedType pt) return getClassFrom(pt.getRawType());
		if (type instanceof GenericArrayType gat) return getClassFrom(gat.getGenericComponentType());
		throw new IllegalArgumentException(type.getClass() + ": " + type);
	}
}
