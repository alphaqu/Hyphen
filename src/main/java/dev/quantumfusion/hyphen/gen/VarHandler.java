package dev.quantumfusion.hyphen.gen;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

public class VarHandler {
	private final Map<String, Integer> variables = new HashMap<>();
	private final Map<String, Type> variableTypes = new HashMap<>();
	private final MethodVisitor methodVisitor;

	public VarHandler(MethodVisitor methodVisitor) {
		this.methodVisitor = methodVisitor;
	}

	public int getVar(String name) {
		if (!variables.containsKey(name))
			throw new RuntimeException("Variable " + name + " does not exist\n " + variables);

		return variables.get(name);
	}

	public Type getVarType(String name) {
		if (!variables.containsKey(name))
			throw new RuntimeException("Variable " + name + " does not exist\n " + variables);

		return variableTypes.get(name);
	}

	public void IntInsnVar(String name, int opcode) {
		methodVisitor.visitIntInsn(opcode, getVar(name));
	}

	public int createOrGetVar(String name, Class<?> clazz) {
		if (variables.containsKey(name))
			return variables.get(name);

		return createVar(name, Type.getType(clazz));
	}


	public int createVar(String name, Class<?> clazz) {
		return createVar(name, Type.getType(clazz));
	}

	public int createVar(String name, Type type) {
		if (variables.containsKey(name))
			throw new RuntimeException("Variable " + name + " already exists\n " + variables);
		final int pos = variables.size();
		variables.put(name, pos);
		variableTypes.put(name, type);
		return pos;
	}

	public void applyLocals(MethodVisitor mv, Label start, Label stop) {
		variables.forEach((s, pos) -> {
			Type type = variableTypes.get(s);
			mv.visitLocalVariable(s, type.getDescriptor(), null, start, stop, pos);
		});
	}
}
