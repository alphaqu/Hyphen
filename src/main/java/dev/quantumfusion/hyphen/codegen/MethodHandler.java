package dev.quantumfusion.hyphen.codegen;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static dev.quantumfusion.hyphen.util.GenUtil.getMethodDesc;
import static dev.quantumfusion.hyphen.util.GenUtil.getVoidMethodDesc;
import static org.objectweb.asm.Opcodes.*;

public class MethodHandler extends MethodVisitor implements AutoCloseable {
	private final IOHandler io;

	// ================================== CREATE ==================================
	public MethodHandler(MethodVisitor mv, IOHandler io) {
		super(Opcodes.ASM9, mv);
		this.io = io;
	}

	public static MethodHandler createVoid(ClassWriter cw, IOHandler io, int tag, String name, Class<?>... param) {
		final MethodVisitor mv = cw.visitMethod(tag, name, getVoidMethodDesc(param), null, null);
		return new MethodHandler(mv, io);
	}

	public static MethodHandler create(ClassWriter cw, IOHandler io, int tag, String name, Class<?> returnClazz, Class<?>... param) {
		final MethodVisitor mv = cw.visitMethod(tag, name, getMethodDesc(returnClazz, param), null, null);
		return new MethodHandler(mv, io);
	}

	// ================================== CLAZZY ====================================
	public void getType(int opcode, Class<?> type) {
		super.visitTypeInsn(opcode, Type.getInternalName(type));
	}

	public void getField(int opcode, Class<?> owner, String name, Class<?> clazz) {
		super.visitFieldInsn(opcode, Type.getInternalName(owner), name, Type.getDescriptor(clazz));
	}

	public void callMethod(int opcode, Class<?> owner, String name, Class<?> descriptor, boolean isInterface) {
		super.visitMethodInsn(opcode, Type.getInternalName(owner), name, Type.getDescriptor(descriptor), isInterface);
	}

	public void callInstanceMethod(Class<?> owner, String name, Class<?> descriptor) {
		callMethod(INVOKEVIRTUAL, owner, name, descriptor, false);
	}

	public void callSpecialMethod(Class<?> owner, String name, Class<?> descriptor) {
		callMethod(INVOKESPECIAL, owner, name, descriptor, false);
	}

	public void callStaticMethod(Class<?> owner, String name, Class<?> descriptor) {
		callMethod(INVOKESTATIC, owner, name, descriptor, false);
	}

	public void callInterfaceMethod(Class<?> owner, String name, Class<?> descriptor) {
		callMethod(INVOKEINTERFACE, owner, name, descriptor, true);
	}

	public void createMultiArray(Class<?> descriptor, int numDimensions) {
		super.visitMultiANewArrayInsn(Type.getDescriptor(descriptor), numDimensions);
	}

	// ==================================== IO ====================================
	public void callIOGet(Class<?> clazz) {
		String desc;
		if (clazz.isArray()) desc = getMethodDesc(clazz, int.class);
		else desc = getMethodDesc(clazz);

		invokeIO(desc, "get" + getSuffix(clazz));
	}

	public void callIOPut(Class<?> clazz) {
		invokeIO(getVoidMethodDesc(clazz), "put" + getSuffix(clazz));
	}

	private void invokeIO(String desc, String name) {
		this.visitMethodInsn(INVOKEVIRTUAL, io.internalName, name, desc, io.isInterface);
	}

	private String getSuffix(Class<?> clazz) {
		String simpleName;
		if (clazz.isArray())
			simpleName = clazz.getComponentType().getSimpleName() + "Array";
		else
			simpleName = clazz.getSimpleName();

		return simpleName.substring(0, 1).toUpperCase() + simpleName.substring(1);
	}

	// ================================= CLOSABLE =================================
	@Override
	public void close() {
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
}
