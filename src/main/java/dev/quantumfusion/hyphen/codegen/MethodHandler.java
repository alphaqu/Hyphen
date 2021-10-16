package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class MethodHandler extends MethodVisitor implements AutoCloseable {
	private final Map<String, Variable> variableMap = new LinkedHashMap<>();
	private final Label start = new Label();
	public final String self;

	public MethodHandler(MethodVisitor methodVisitor, String self) {
		super(ASM9, methodVisitor);
		this.self = self;
		this.visitCode();
		this.visitLabel(start);
	}

	// Classification:tm:
	public void visitTypeInsn(int opcode, Class<?> type) {
		super.visitTypeInsn(opcode, GenUtil.internal(type));
	}

	public void visitFieldInsn(int opcode, Class<?> owner, String name, Class<?> descriptor) {
		super.visitFieldInsn(opcode, GenUtil.internal(owner), name, GenUtil.desc(descriptor));
	}

	public void visitMethodInsn(int opcode, Class<?> owner, String name, Class<?> returnClass, Class<?> parameters) {
		super.visitMethodInsn(opcode, GenUtil.internal(owner), name, GenUtil.methodDesc(returnClass, parameters), opcode == INVOKEINTERFACE);
	}

	public void visitMethodInsn(MethodInfo info) {
		super.visitMethodInsn(INVOKESTATIC, self, info.getName(), GenUtil.methodDesc(info.returnClass, info.parameters), false);
	}

	// Elegentification:tm:
	public void op(int... op) {
		for (int i : op) this.visitInsn(i);
	}

	public void varOp(int op, String... vars) {
		for (String varName : vars) varOp(op, varName);
	}

	public void varOp(int op, String varName) {
		var var = getVar(varName);
		this.visitIntInsn(var.type().getOpcode(op), var.pos());
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
		for (var entry : variableMap.entrySet()) {
			var var = entry.getValue();
			this.visitLocalVariable(entry.getKey(), var.type().getDescriptor(), null, start, stop, var.pos());
		}
		this.visitMaxs(0, 0);
		this.visitEnd();
	}
}