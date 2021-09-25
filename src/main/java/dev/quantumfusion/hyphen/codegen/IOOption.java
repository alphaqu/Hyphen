package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.io.ArrayIO;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.io.UnsafeIO;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static dev.quantumfusion.hyphen.util.GenUtil.getMethodDesc;
import static dev.quantumfusion.hyphen.util.GenUtil.getVoidMethodDesc;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public enum IOOption {
	BYTEBUFFER(ByteBufferIO.class, false),
	/**
	 * Generate bytecode that accepts the UnsafeIO directly.
	 */
	UNSAFE(UnsafeIO.class, false),
	/**
	 * Generate bytecode that accepts the ArrayIO directly.
	 */
	ARRAY(ArrayIO.class, false),
	/**
	 * Generate bytecode that accepts the IOInterface interface. For custom implementations.
	 */
	CUSTOM(IOInterface.class, true);


	public final Class<?> ioClass;
	private final String internalName;
	private final boolean isInterface;

	IOOption(Class<?> ioClass, boolean isInterface) {
		this.ioClass = ioClass;
		this.internalName = Type.getInternalName(ioClass);
		this.isInterface = isInterface;
	}

	public void invokeGetMethod(MethodVisitor mv, Class<?> clazz) {
		String desc;
		if (clazz.isArray()) desc = getMethodDesc(clazz, int.class);
		else desc = getMethodDesc(clazz);

		invoke(mv, desc, "get" + getSuffix(clazz));
	}

	public void invokePutMethod(MethodVisitor mv, Class<?> clazz) {
		invoke(mv, getVoidMethodDesc(clazz), "put" + getSuffix(clazz));
	}

	private void invoke(MethodVisitor mv, String desc, String name) {
		mv.visitMethodInsn(INVOKEVIRTUAL, internalName, name, desc, isInterface);
	}

	private String getSuffix(Class<?> clazz) {
		String simpleName;
		if (clazz.isArray())
			simpleName = clazz.getComponentType().getSimpleName() + "Array";
		else
			simpleName = clazz.getSimpleName();

		return simpleName.substring(0, 1).toUpperCase() + simpleName.substring(1);
	}

}
