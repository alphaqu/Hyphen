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

	private static final char[] HYPHEN_METHOD_BASE_CHARS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_', '$'};

	public static String hyphenShortMethodName(int methodId) {
		StringBuilder builder = new StringBuilder();
		if (methodId == 0) builder.append('A');
		// ishland big brain
		while(methodId != 0) {
			builder.append(HYPHEN_METHOD_BASE_CHARS[(methodId & 63)]);
			methodId >>= 6;
		}
		return builder.toString();
	}
}
