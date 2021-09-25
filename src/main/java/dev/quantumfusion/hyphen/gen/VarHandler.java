package dev.quantumfusion.hyphen.gen;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VarHandler {
	private static final boolean NAME_DEDUP = true;

	public record Var(String name, int index, Type type, String internalName) {
		public void visitIntInsn(MethodVisitor mv, int opcode) {
			mv.visitIntInsn(opcode, this.index());
		}
	}

	private final Map<String, Integer> nameDedup = NAME_DEDUP ? new HashMap<>() : null;

	private static final Var SCOPE_MARKER = new Var("SCOPE_MARKER", -1, null, "");

	private int currentIndex = 0;

	private final List<Var> vars = new ArrayList<>();
	private final List<Label> scopeStarts = new ArrayList<>();

	private final MethodVisitor methodVisitor;

	public VarHandler(MethodVisitor methodVisitor) {
		this.methodVisitor = methodVisitor;
	}

	public Var getVarOrNull(String name) {
		for (Var var : this.vars) {
			if (var.name().equals(name)) {
				return var;
			}
		}
		return null;
	}

	public Label createScope() {
		this.vars.add(SCOPE_MARKER);
		Label start = new Label();
		this.scopeStarts.add(start);
		return start;
	}

	public Label pushScope() {
		Label start = this.createScope();
		this.methodVisitor.visitLabel(start);
		return start;
	}

	public void popScope() {
		this.popScope(new Label());
	}

	public void popScope(Label stop) {
		Label start = this.scopeStarts.remove(this.scopeStarts.size() - 1);
		this.methodVisitor.visitLabel(stop);

		for (int i = this.vars.size() - 1; i >= 0; i--) {
			Var var = this.vars.remove(i);
			if (var.index() == -1) return;
			this.currentIndex--;
			this.methodVisitor.visitLocalVariable(var.internalName(), var.type().getDescriptor(), null, start, stop, var.index());
		}
	}

	public Var getVar(String name) {
		Var var = this.getVarOrNull(name);
		if (var != null)
			return var;
		throw new RuntimeException("Variable " + name + " does not exist\n " + this.vars);
	}

	public void IntInsnVar(Var var, int opcode) {
		this.methodVisitor.visitIntInsn(opcode, var.index);
	}

	public void IntInsnVar(String name, int opcode) {
		this.IntInsnVar(this.getVar(name), opcode);
	}

	public Var createOrGetVar(String name, Class<?> clazz) {
		Var var = this.getVarOrNull(name);
		if (var != null) return var;

		return this.createVarInternal(name, Type.getType(clazz));
	}

	private Var createVarInternal(String name, Type type) {
		Var var;
		if (NAME_DEDUP) {
			var = new Var(name, this.currentIndex++, type, name + this.nameDedup.merge(name, 1, Integer::sum));
		} else {
			var = new Var(name, this.currentIndex++, type, name);
		}
		this.vars.add(var);
		return var;
	}

	public Var createVar(String name, Class<?> clazz) {
		return this.createVar(name, Type.getType(clazz));
	}

	public boolean existsInScope(String name) {
		for (int i = this.vars.size() - 1; i >= 0; i--) {
			Var var = this.vars.get(i);
			if (var.index == -1) return false;
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
