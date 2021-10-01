package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.util.GenUtil;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.quantumfusion.hyphen.util.GenUtil.getMethodDesc;
import static dev.quantumfusion.hyphen.util.GenUtil.getVoidMethodDesc;
import static org.objectweb.asm.Opcodes.*;

@SuppressWarnings("WeakerAccess")
public class MethodHandler extends MethodVisitor implements AutoCloseable {
	private final IOMode io;
	public final Class<?> returnClazz;
	private final CodegenHandler codegenHandler;

	// ================================== CREATE ==================================
	public MethodHandler(MethodVisitor mv, IOMode io, Class<?> returnClazz, CodegenHandler codegenHandler) {
		super(Opcodes.ASM9, mv);
		this.io = io;
		this.returnClazz = returnClazz;
		this.codegenHandler = codegenHandler;
		this.pushScope();
	}

	public static MethodHandler createVoid(CodegenHandler codegenHandler, IOMode io, int tag, String name, Class<?>... param) {
		final MethodVisitor mv = codegenHandler.cw.visitMethod(tag, name, getVoidMethodDesc(param), null, null);
		return new MethodHandler(mv, io, Void.TYPE, codegenHandler);
	}

	public static MethodHandler create(CodegenHandler codegenHandler, IOMode io, int tag, String name, Class<?> returnClazz, Class<?>... param) {
		final MethodVisitor mv = codegenHandler.cw.visitMethod(tag, name, getMethodDesc(returnClazz, param), null, null);
		return new MethodHandler(mv, io, returnClazz, codegenHandler);
	}

	// ================================== CLAZZY ====================================
	public void typeInsn(int opcode, Class<?> type) {
		super.visitTypeInsn(opcode, Type.getInternalName(type));
	}

	public void getField(int opcode, Class<?> owner, String name, Class<?> clazz) {
		super.visitFieldInsn(opcode, Type.getInternalName(owner), name, Type.getDescriptor(clazz));
	}

	public void callMethod(int opcode, Class<?> owner, String name, boolean isInterface, @Nullable Class<?> returnClass, Class<?>... parameters) {
		super.visitMethodInsn(opcode, Type.getInternalName(owner), name, GenUtil.getMethodDesc(returnClass, parameters), isInterface);
	}

	public void callInstanceMethod(Class<?> owner, String name, @Nullable Class<?> returnClass, Class<?>... parameters) {
		this.callMethod(INVOKEVIRTUAL, owner, name, false, returnClass, parameters);
	}

	public void callSpecialMethod(Class<?> owner, String name, @Nullable Class<?> returnClass, Class<?>... parameters) {
		this.callMethod(INVOKESPECIAL, owner, name, false, returnClass, parameters);
	}

	public void callStaticMethod(Class<?> owner, String name, @Nullable Class<?> returnClass, Class<?>... parameters) {
		this.callMethod(INVOKESTATIC, owner, name, false, returnClass, parameters);
	}

	public void callInterfaceMethod(Class<?> owner, String name, @Nullable Class<?> returnClass, Class<?>... parameters) {
		this.callMethod(INVOKEINTERFACE, owner, name, true, returnClass, parameters);
	}

	public void callInternalStaticMethod(String name, @Nullable Class<?> returnClass, Class<?>... parameters) {
		this.visitMethodInsn(INVOKESTATIC, this.codegenHandler.name, name, GenUtil.getMethodDesc(returnClass, parameters), false);
	}

	public void createMultiArray(Class<?> descriptor, int numDimensions) {
		super.visitMultiANewArrayInsn(Type.getDescriptor(descriptor), numDimensions);
	}

	// =================================== UTIL ===================================
	public void returnOp() {
		this.visitInsn(Type.getType(returnClazz).getOpcode(IRETURN));
	}

	public void cast(Class<?> clazz) {
		this.visitTypeInsn(CHECKCAST, Type.getInternalName(clazz));
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

	public Class<?> getIOClazz(){
		return this.io.ioClass;
	}

	private void invokeIO(String desc, String name) {
		this.visitMethodInsn(INVOKEVIRTUAL, io.internalName, name, desc, false);
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
		this.popScope();
		this.mv.visitMaxs(0, 0);
		this.mv.visitEnd();
	}

	// ================================ VARHANDLER ================================
	public final class Var {
		private final String name;
		private final int index;
		private final Type type;
		private final String internalName;

		private Var(String name, int index, Type type, String internalName) {
			this.name = name;
			this.index = index;
			this.type = type;
			this.internalName = internalName;
		}

		public void load() {
			this.inst(ILOAD);
		}

		public void store() {
			this.inst(ISTORE);
		}

		public void loadFromArray() {
			this.inst(IALOAD);
		}

		public void storeToArray() {
			this.inst(IASTORE);
		}

		public void inst(int op) {
			MethodHandler.this.visitIntInsn(this.type.getOpcode(op), this.index);
		}

		public void iinc(int value) {
			MethodHandler.this.visitIincInsn(this.index, value);
		}
	}

	// just for visual decompilation
	private static final boolean NAME_DEDUP = true;
	private final Map<String, Integer> nameDedup = NAME_DEDUP ? new HashMap<>() : null;

	private int currentIndex = 0;

	private final List<@Nullable Var> vars = new ArrayList<>();
	private final List<Label> scopeStarts = new ArrayList<>();

	public Var getVarOrNull(String name) {
		for (Var var : this.vars) {
			if (var != null && var.name.equals(name)) {
				return var;
			}
		}
		return null;
	}

	public Label createScope() {
		this.vars.add(null);
		Label start = new Label();
		this.scopeStarts.add(start);
		return start;
	}

	public Label pushScope() {
		Label start = this.createScope();
		this.visitLabel(start);
		return start;
	}

	public void popScope() {
		this.popScope(new Label());
	}

	public void popScope(Label stop) {
		Label start = this.scopeStarts.remove(this.scopeStarts.size() - 1);
		this.visitLabel(stop);

		for (int i = this.vars.size() - 1; i >= 0; i--) {
			Var var = this.vars.remove(i);
			if (var == null) return;
			this.currentIndex--;
			this.visitLocalVariable(var.internalName, var.type.getDescriptor(), null, start, stop, var.index);
		}
	}

	public Var getVar(String name) {
		Var var = this.getVarOrNull(name);
		if (var != null)
			return var;
		throw new RuntimeException("Variable " + name + " does not exist\n " + this.vars);
	}

	public Var createOrGetVar(String name, Class<?> clazz) {
		Var var = this.getVarOrNull(name);
		if (var != null) return var;

		return this.createVarInternal(name, Type.getType(clazz));
	}

	private Var createVarInternal(String name, Type type) {
		String internalName;
		if (NAME_DEDUP) {
			if (this.nameDedup.containsKey(name)) {
				internalName = name + this.nameDedup.merge(name, 1, Integer::sum);
			} else {
				this.nameDedup.put(name, 0);
				internalName = name;
			}

		} else internalName = name;

		Var var = new Var(name, this.currentIndex++, type, internalName);

		this.vars.add(var);
		return var;
	}

	public Var createVar(String name, Class<?> clazz) {
		return this.createVar(name, Type.getType(clazz));
	}

	public boolean existsInScope(String name) {
		for (int i = this.vars.size() - 1; i >= 0; i--) {
			Var var = this.vars.get(i);
			if (var == null) return false;
			if (var.name.equals(name)) return true;
		}
		return false;
	}

	public Var createVar(String name, Type type) {
		if (this.existsInScope(name))
			throw new RuntimeException("Variable " + name + " already exists in scope\n " + this.vars);
		return this.createVarInternal(name, type);
	}
}
