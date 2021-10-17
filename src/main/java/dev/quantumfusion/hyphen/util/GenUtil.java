package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

@SuppressWarnings("WeakerAccess")
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

	public static void shouldCastGeneric(MethodHandler mh, Clazz clazz) {
		if (clazz.getDefinedClass() != clazz.getBytecodeClass()) {
			mh.visitTypeInsn(CHECKCAST, clazz.getDefinedClass());
		}
	}

	public static String methodDesc(Class<?> returnClass, Class<?>... parameters) {
		return Type.getMethodDescriptor(of(returnClass), of(parameters));
	}

	private static final char[] HYPHEN_METHOD_BASE_CHARS = "_$0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	public static String hyphenShortMethodName(int methodId) {
//		if (methodId == 0) {
//			return """
//					                       \n
//	public static Type[] of(Class<?>[] classes) {
//		var out = new Type[classes.length];
//		for (int i = 0; i < classes.length; i++)
//			out[i] = of(classes[i]);
//		return out;
//	}
//
//	public static Type of(Class<?> aClass) {
//		return Type.getType(aClass);
//	}
//
//	public static String internal(Class<?> aClass) {
//		return Type.getInternalName(aClass);
//	}
//
//	public static String desc(Class<?> aClass) {
//		return Type.getDescriptor(aClass);
//	}
//
//	public static String methodDesc(Class<?> returnClass, Class<?>... parameters) {
//		return Type.getMethodDescriptor(of(returnClass), of(parameters));
//	}
//					                       \n
//					""".replace('.', ' ')
//					.replace(';', '\u037E')
//					.replace(':', '\u02D0')
//					.replace('[', ']')
//					.replace('<', '{')
//					.replace('>', '}')
//					//. ; [ / < > :
//					;
//		}

		final int length = (63 - Long.numberOfLeadingZeros(methodId)) / 6 + 1;
		final char[] result = new char[length];
		for (int i = length - 1; i >= 0; i--) {
			result[i] = HYPHEN_METHOD_BASE_CHARS[methodId & 63];
			methodId >>= 6;
		}
		return new String(result);
	}

	public static String upperCase(String str) {
		return str.substring(0,1).toUpperCase() + str.substring(1);
	}


	public static void putIO(MethodHandler mh, Class<?> primitive) {
		mh.visitMethodInsn(INVOKEVIRTUAL, mh.ioClass, "put" + getName(primitive), Void.TYPE, primitive);
	}

	public static void getIO(MethodHandler mh, Class<?> primitive) {
		mh.visitMethodInsn(INVOKEVIRTUAL, mh.ioClass, "get" + getName(primitive), primitive);
	}

	private static String getName(Class<?> primitive) {
		if (primitive.isArray())
			return GenUtil.upperCase(primitive.getComponentType().getSimpleName()) + "Array";
		return GenUtil.upperCase(primitive.getSimpleName());
	}
}
