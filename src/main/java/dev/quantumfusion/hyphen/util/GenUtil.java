package dev.quantumfusion.hyphen.util;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

public class GenUtil {

	public static String methodDesc(@Nullable Class<?> returnClass, Class<?>... parameters) {
		return Type.getMethodDescriptor(getType(returnClass), getType(parameters));
	}

	public static Type getType(@Nullable Class<?> clazz) {
		return Type.getType(voidNullable(clazz));
	}


	public static Type[] getType(@Nullable Class<?>... clazzes) {
		final Type[] types = new Type[clazzes.length];
		for (int i = 0, clazzesLength = clazzes.length; i < clazzesLength; i++) types[i] = getType(clazzes[i]);
		return types;
	}

	public static Class<?> voidNullable(@Nullable Class<?> clazz) {
		return clazz == null ? Void.TYPE : clazz;
	}



}
