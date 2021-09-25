package dev.quantumfusion.hyphen.util;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

public class GenUtil {

	public static String getMethodDesc(@Nullable Class<?> returnClazz, Class<?>... param) {
		Type returnType = returnClazz == null ? Type.VOID_TYPE : Type.getType(returnClazz);

		if (param.length == 0) return Type.getMethodDescriptor(returnType);
		Type[] params = new Type[param.length];
		for (int i = 0; i < param.length; i++)
			params[i] = Type.getType(param[i]);

		return Type.getMethodDescriptor(returnType, params);
	}

	public static String getVoidMethodDesc(Class<?>... param) {
		return getMethodDesc(Void.TYPE, param);
	}

}
