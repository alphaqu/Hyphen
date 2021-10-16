package dev.quantumfusion.hyphen.util;

import org.objectweb.asm.Type;

public final class GenUtil {
	public static Type[] of(Class<?>[] classes) {
		var out = new Type[classes.length];
		for (int i = 0; i < classes.length; i++)
			out[i] = of(classes[i]);
		return out;
	}

	public static Type of(Class<?> aClass) {
		return Type.getType(aClass);
	}

	public static String internal(Class<?> aClass) {
		return Type.getInternalName(aClass);
	}

	public static String desc(Class<?> aClass) {
		return Type.getDescriptor(aClass);
	}

	public static String methodDesc(Class<?> returnClass, Class<?>... parameters) {
		return Type.getMethodDescriptor(of(returnClass), of(parameters));
	}
}
