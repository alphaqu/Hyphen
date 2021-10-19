package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;

import static org.objectweb.asm.Opcodes.*;

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
		shouldCastGeneric(mh, clazz.getDefinedClass(), clazz.getBytecodeClass());
	}

	public static void shouldCastGeneric(MethodHandler mh, Class<?> actual, Class<?> bytecodeClass) {
		if (!actual.isAssignableFrom(bytecodeClass)) {
			mh.typeOp(CHECKCAST, actual);
		}
	}

	public static String methodDesc(Class<?> returnClass, Class<?>... parameters) {
		return Type.getMethodDescriptor(of(returnClass), of(parameters));
	}

	private static final char[] HYPHEN_METHOD_BASE_CHARS = "ඞabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ$0123456789".toCharArray();

	public static String hyphenShortMethodName(int methodId) {
		final int length = (63 - Long.numberOfLeadingZeros(methodId)) / 6 + 1;
		final char[] result = new char[length];
		for (int i = length - 1; i >= 0; i--) {
			result[i] = HYPHEN_METHOD_BASE_CHARS[methodId & 63];
			methodId >>= 6;
		}
		return new String(result);
	}

	public static String upperCase(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public static String makeSafe(String str){
		return str
				.replace('[', '⟦')
				.replace(']', '⟧')
				.replace('<', '❮')
				.replace('>', '❯')
				.replace('(', '❪')
				.replace(')', '❫')
				.replace('/', '∕')
				.replace('.', '•')
				.replace(';', '\u037E') // greek question mark, intellij "fixes" it for you
				.replace(':', 'ː');
	}
}
