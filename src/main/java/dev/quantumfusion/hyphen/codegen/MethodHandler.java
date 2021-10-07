package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.util.GenUtil;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class MethodHandler extends MethodVisitor implements AutoCloseable {
	private static final int DEFAULT_ACCESS = ACC_PUBLIC | ACC_FINAL | ACC_STATIC;
	private final Map<String, Variable> vars = new HashMap<>();
	private final CodegenHandler ch;

	public MethodHandler(CodegenHandler ch, int access, String name, Class<?> returnClass, Class<?>... parameters) {
		super(ASM9, ch.visitMethod(access, name, GenUtil.methodDesc(returnClass, parameters), null, null));
		this.ch = ch;
	}

	public MethodHandler(CodegenHandler ch, String name, Class<?> returnClass, Class<?>... parameters) {
		this(ch, DEFAULT_ACCESS, name, returnClass, parameters);
	}

	public MethodHandler(CodegenHandler ch, MethodInfo info) {
		this(ch, DEFAULT_ACCESS, info.name, info.returnClass, info.parameters);
	}

	public Variable getVar(String name) {
		if (!vars.containsKey(name))
			throw new RuntimeException("Variable " + name + " does not exist.");

		return vars.get(name);
	}

	public void visitVars(int opcode, String... vars) {
		for (String var : vars) {
			final Variable variable = getVar(var);
			super.visitIntInsn(variable.op(opcode), variable.id());
		}
	}

	public void visitInsn(int... opcode) {
		for (int i : opcode) super.visitInsn(i);
	}

	public void visitMethodInsn(int opcode, Class<?> owner, String name, @Nullable Class<?> returnClass, Class<?>... parameters) {
		returnClass = GenUtil.voidNullable(returnClass);
		try {
			final Method method = owner.getMethod(name, parameters);
			if (method.getReturnType() != returnClass)
				throw new NoSuchMethodException("Return is " + returnClass.getSimpleName() + " but found " + method.getReturnType().getSimpleName());

			super.visitMethodInsn(opcode, Type.getInternalName(owner), name, Type.getMethodDescriptor(method), opcode == Opcodes.INVOKEINTERFACE);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public void visitSelfMethodInsn(int opcode, String name, @Nullable Class<?> returnClass, Class<?>... parameters) {
		super.visitMethodInsn(opcode, this.ch.className, name, GenUtil.methodDesc(returnClass, parameters), opcode == Opcodes.INVOKEINTERFACE);
	}

	public void visitFieldInsn(int opcode, Class<?> owner, String name, Class<?> clazz) {
		super.visitFieldInsn(opcode, Type.getDescriptor(owner), name, Type.getDescriptor(clazz));
	}

	@Override
	public void close() {
		this.visitEnd();
	}
}
