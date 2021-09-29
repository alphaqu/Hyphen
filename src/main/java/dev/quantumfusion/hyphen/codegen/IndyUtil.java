package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.invoke.*;

import static dev.quantumfusion.hyphen.util.GenUtil.getMethodDesc;
import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;

public final class IndyUtil {
	private static final Handle LAMBDA_METAFACTORY_HANDLE = new Handle(
			H_INVOKESTATIC,
			Type.getInternalName(LambdaMetafactory.class),
			"metafactory",
			getMethodDesc(
					CallSite.class, // pushed by vm
					MethodHandles.Lookup.class, // pushed by vm
					String.class, // pushed by vm
					MethodType.class, // sam type
					MethodType.class,
					MethodHandle.class,
					MethodType.class
			),
			false
	);

	public static void createMethodRef(MethodVisitor mv, Class<?> targetInterface, String targetMethod, String descriptor, boolean instanced, String sourceClass, String sourceMethod, String resultDescriptor, Class<?>... capturedLocals) {
		assert !instanced;

		mv.visitInvokeDynamicInsn(
				targetMethod,
				GenUtil.getMethodDesc(targetInterface, capturedLocals),
				LAMBDA_METAFACTORY_HANDLE,
				Type.getMethodType(resultDescriptor),
				new Handle(
						H_INVOKESTATIC,
						sourceClass,
						sourceMethod,
						descriptor,
						false
				),
				Type.getMethodType(resultDescriptor)
		);
	}
}
