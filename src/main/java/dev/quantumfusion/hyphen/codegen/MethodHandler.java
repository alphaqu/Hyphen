package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class MethodHandler extends MethodVisitor implements AutoCloseable {
	private static final char SHORT_VAR_NAME = '\u0D9E'; // amogus
	private final Map<String, Variable> variableMap = new LinkedHashMap<>();
	private final Label start = new Label();
	public final String self;
	public final Class<?> dataClass;
	public final Class<?> ioClass;
	private boolean compactVars;

	public MethodHandler(MethodVisitor methodVisitor, String self, Class<?> dataClass, Class<?> ioClass) {
		super(ASM9, methodVisitor);
		this.self = self;
		this.dataClass = dataClass;
		this.ioClass = ioClass;
		this.compactVars = false;

		this.visitCode();
		this.visitLabel(start);
	}

	public MethodHandler(ClassWriter cw, MethodInfo methodInfo, String self, Class<?> dataClass, Class<?> ioClass, boolean raw, boolean compactVars) {
		this(cw.visitMethod(ACC_PUBLIC | (raw ? 0 : ACC_STATIC) | ACC_FINAL,
				methodInfo.getName(),
				GenUtil.methodDesc(convert(methodInfo.returnClass, raw), parameters(methodInfo.parameters, raw)),
				null, null), self, dataClass, ioClass);

		if (raw) this.addVar("this", Object.class);
		this.compactVars = compactVars;
	}

	private static Class<?>[] parameters(Class<?>[] parameters, boolean raw) {
		if (raw) {
			final Class<?>[] a = new Class[parameters.length];
			Arrays.fill(a, Object.class);
			return a;
		}
		return parameters;
	}

	private static Class<?> convert(Class<?> parameters, boolean raw) {
		if (raw) if (!parameters.isPrimitive()) return Object.class;
		return parameters;
	}


	// Classification:tm:
	public void typeOp(int opcode, Class<?> type) {
		super.visitTypeInsn(opcode, GenUtil.internal(type));
	}

	public void visitFieldInsn(int opcode, Class<?> owner, String name, Class<?> descriptor) {
		super.visitFieldInsn(opcode, GenUtil.internal(owner), name, GenUtil.desc(descriptor));
	}

	public void callInst(int opcode, Class<?> owner, String name, Class<?> returnClass, Class<?>... parameters) {
		super.visitMethodInsn(opcode, GenUtil.internal(owner), name, GenUtil.methodDesc(returnClass, parameters), opcode == INVOKEINTERFACE);
	}

	public void callInst(MethodInfo info) {
		super.visitMethodInsn(INVOKESTATIC, self, info.getName(), GenUtil.methodDesc(info.returnClass, info.parameters), false);
	}

	// Elegentification:tm:
	public void op(int... op) {
		for (int i : op) this.visitInsn(i);
	}

	public void varOp(int op, String... vars) {
		for (var varName : vars) varOp(op, varName);
	}

	public void varOp(int op, Variable... vars) {
		for (var var : vars) varOp(op, var);
	}

	public void varOp(int op, String var) {
		varOp(op, getVar(var));
	}

	public void varOp(int op, Variable var) {
		this.visitIntInsn(var.type().getOpcode(op), var.pos());
	}

	// IOgentification:tm:
	public void getIO(Class<?> primitive) {
		this.callInst(INVOKEVIRTUAL, this.ioClass, "get" + getIOName(primitive), primitive);
	}

	public void putIO(Class<?> primitive) {
		this.callInst(INVOKEVIRTUAL, this.ioClass, "put" + getIOName(primitive), Void.TYPE, primitive);
	}

	private static String getIOName(Class<?> primitive) {
		if (primitive.isArray())
			return GenUtil.upperCase(primitive.getComponentType().getSimpleName()) + "Array";
		return GenUtil.upperCase(primitive.getSimpleName());
	}


	// Var things
	public Variable addVar(String name, Class<?> type) {
		if (variableMap.containsKey(name))
			return variableMap.get(name);

		var var = new Variable(variableMap.size(), Type.getType(type));
		variableMap.put(name, var);
		return var;
	}

	public Variable getVar(String name) {
		var var = variableMap.get(name);
		if (var == null) throw new RuntimeException("Variable " + name + " does not exist.");
		return var;
	}

	// Close operations to use this in a try catch. NO RETURN HAPPENS HERE
	@Override
	public void close() {
		Label stop = new Label();
		this.visitLabel(stop);

		int i = 0;
		for (var entry : variableMap.entrySet()) {
			var var = entry.getValue();
			final String name = compactVars ? String.valueOf(SHORT_VAR_NAME) : entry.getKey();
			this.visitLocalVariable(name, var.type().getDescriptor(), null, start, stop, var.pos());
		}
		this.visitMaxs(0, 0);
		this.visitEnd();
	}
}