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
	public final String self;
	public final Class<?> dataClass;
	public final Class<?> ioClass;
	private final Map<String, Variable> variableMap = new LinkedHashMap<>();
	private final Label start = new Label();
	private final boolean instanceMethod;
	private boolean compactVars;

	public MethodHandler(MethodVisitor methodVisitor, String self, Class<?> dataClass, Class<?> ioClass, boolean instanceMethod) {
		super(ASM9, methodVisitor);
		this.self = self;
		this.dataClass = dataClass;
		this.ioClass = ioClass;
		this.instanceMethod = instanceMethod;
		this.compactVars = false;

		this.visitCode();
		this.visitLabel(start);
	}

	public MethodHandler(
			ClassWriter cw,
			MethodInfo methodInfo,
			String self,
			Class<?> dataClass, Class<?> ioClass,
			boolean compactVars,
			boolean spark, boolean synthetic
	) {
		this(cw.visitMethod(ACC_PUBLIC | ACC_FINAL | (spark ? 0 : ACC_STATIC) | (synthetic ? ACC_SYNTHETIC : 0),
							methodInfo.getName(),
							GenUtil.methodDesc(convert(methodInfo.returnClass, spark), parameters(methodInfo.parameters, spark)),
							null, null), self, dataClass, ioClass, spark);

		if (spark) this.addVar("this", Object.class);
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
		super.visitMethodInsn(opcode, GenUtil.internal(owner), name, GenUtil.methodDesc(returnClass, parameters), owner.isInterface());
	}

	public void callInst(MethodInfo info) {
		super.visitMethodInsn(INVOKESTATIC, self, info.getName(), GenUtil.methodDesc(info.returnClass, info.parameters), false);
	}

	// Elegentification:tm:
	public void op(int... op) {
		for (int i : op) this.visitInsn(i);
	}

	public void parameterOp(int op, int parameter) {
		varOp(op, getParamName(parameter));
	}

	public void varOp(int op, Variable... vars) {
		for (var var : vars) varOp(op, var);
	}

	void varOp(int op, String var) {
		varOp(op, getVar(var));
	}

	public void varOp(int op, Variable var) {
		this.visitIntInsn(var.type().getOpcode(op), var.pos());
	}

	public void loadIO() {
		this.parameterOp(ILOAD, 0);
	}

	public static String getParamName(int id) {
		return "methodparam_" + id;
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

	// Label things
	public Label jump(int op) {
		final Label label = new Label();
		this.visitJumpInsn(op, label);
		return label;
	}

	public void jump(int op, Label label) {
		this.visitJumpInsn(op, label);
	}

	public void defineLabel(Label label) {
		this.visitLabel(label);
	}

	public Label defineLabel() {
		final Label label = new Label();
		this.visitLabel(label);
		return label;
	}

	// Var things
	public Variable addVar(String name, Class<?> type, int createOp) {
		final Variable i = this.addVar(name, type);
		this.op(createOp);
		this.varOp(ISTORE, i);
		return i;
	}

	public Variable addVar(String name, Class<?> type) {
		if (variableMap.containsKey(name)) {
			int i = 0;
			while (variableMap.containsKey(name + i)) {
				i++;
			}
			name += i;
		}

		var var = new Variable(variableMap.size(), Type.getType(type));
		variableMap.put(name, var);
		return var;
	}

	public Variable getVar(String name) {
		var var = variableMap.get(name);
		if (var == null) throw new RuntimeException("Variable " + name + " does not exist.");
		return var;
	}

	public void inc(Variable var, int size) {
		this.visitIincInsn(var.pos(), size);
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