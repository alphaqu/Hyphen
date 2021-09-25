package dev.quantumfusion.hyphen.util;

import org.objectweb.asm.Type;

public class GenUtil {

	public static String getMethodDesc(Class<?> returnClazz, Class<?>... param) {
		if (param.length == 0) return Type.getMethodDescriptor(Type.getType(returnClazz));
		Type[] params = new Type[param.length];
		for (int i = 0; i < param.length; i++)
			params[i] = Type.getType(param[i]);

		return Type.getMethodDescriptor(Type.getType(returnClazz), params);
	}

	public static String getVoidMethodDesc(Class<?>... param) {
		return getMethodDesc(Void.TYPE, param);
	}

}
