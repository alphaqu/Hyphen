package dev.quantumfusion.hyphen.codegen.statement;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.GOTO;

public class For implements AutoCloseable {
	protected final MethodHandler mh;
	protected final Label start = new Label();
	protected final Label stop = new Label();


	public For(MethodHandler mh) {
		this.mh = mh;
	}

	public For start() {
		this.mh.visitLabel(start);
		return this;
	}

	public void exit(int op) {
		this.mh.visitJumpInsn(op, stop);
	}

	@Override
	public void close() {
		this.mh.visitJumpInsn(GOTO, start);
		this.mh.visitLabel(stop);
	}
}
