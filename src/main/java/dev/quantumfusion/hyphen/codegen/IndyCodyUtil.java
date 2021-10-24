package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;

import java.lang.invoke.*;

import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;

public final class IndyCodyUtil {
	private static final Handle LAMBDA_METAFACTORY_HANDLE = GenUtil.createHandle(
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
				GenUtil.methodDesc(targetInterface, capturedParameters),
				LAMBDA_METAFACTORY_HANDLE,
				GenUtil.methodDesc(targetMethodReturnClass, targetMethodParameters),
				GenUtil.createHandle(
						H_INVOKESTATIC,
						sourceClass,
						sourceMethod,
						false,
						sourceMethodReturnClass,
						ArrayUtil.combine(capturedParameters, uncapturedParameters)
				),
				GenUtil.methodDesc(sourceMethodReturnClass, uncapturedParameters)
		);
	}
}
