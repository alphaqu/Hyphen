package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.invoke.*;
import java.util.Map;

import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;

@SuppressWarnings("WeakerAccess")
public final class GenUtil {
	private static final char[] HYPHEN_METHOD_BASE_CHARS = "ඞabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ$0123456789".toCharArray();
	private static final Handle LAMBDA_METAFACTORY_HANDLE = createHandle(
			H_INVOKESTATIC,
			LambdaMetafactory.class,
			"metafactory",
			false,
			CallSite.class, // pushed by vm
			MethodHandles.Lookup.class, // pushed by vm
			String.class, // pushed by vm
			MethodType.class, // sam type
			MethodType.class,
			MethodHandle.class,
			MethodType.class
	);
	private static final Map<Character, Character> SAFE_METHOD_NAME_MAPPER = Map.of('[', '$', ']', '$',
																					'<', '$', '>', '$',
																					'(', '$', ')', '$',
																					'/', '∕', '.', '•',
																					';', '\u037E', ':', '：');

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

	public static Type methodTypeDesc(Class<?> returnClass, Class<?>... parameters) {
		return Type.getMethodType(of(returnClass), of(parameters));
	}

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

	public static String makeSafe(String str) {
		return str.chars()
				.map(ch -> SAFE_METHOD_NAME_MAPPER.getOrDefault((char) ch, (char) ch))
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	public static Handle createHandle(int op, Class<?> owner, String name, boolean isInterface, Class<?> returnClass, Class<?>... parameters) {
		return new Handle(
				op,
				Type.getInternalName(owner),
				name,
				methodDesc(returnClass, parameters),
				isInterface
		);
	}

	public static Handle createHandle(int op, String owner, String name, boolean isInterface, Class<?> returnClass, Class<?>... parameters) {
		return new Handle(
				op,
				owner,
				name,
				methodDesc(returnClass, parameters),
				isInterface
		);
	}

	public static void createMethodRef(
			MethodVisitor mv,
			Class<?> targetInterface,
			String targetMethod,
			Class<?> targetMethodReturnClass,
			Class<?>[] targetMethodParameters,
			String sourceClass,
			String sourceMethod,
			Class<?> sourceMethodReturnClass,
			Class<?>[] capturedParameters,
			Class<?>[] uncapturedParameters) {
		mv.visitInvokeDynamicInsn(
				targetMethod,
				methodDesc(targetInterface, capturedParameters),
				LAMBDA_METAFACTORY_HANDLE,
				methodTypeDesc(targetMethodReturnClass, targetMethodParameters),
				createHandle(
						H_INVOKESTATIC,
						sourceClass,
						sourceMethod,
						false,
						sourceMethodReturnClass,
						ArrayUtil.combine(capturedParameters, uncapturedParameters)
				),
				methodTypeDesc(sourceMethodReturnClass, uncapturedParameters)
		);
	}
}
