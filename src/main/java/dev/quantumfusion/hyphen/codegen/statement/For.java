package dev.quantumfusion.hyphen.codegen.statement;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.GOTO;

public class For implements AutoCloseable {
	protected final MethodHandler mh;
	protected final Label start = new Label();
	protected final Label stop = new Label();


	protected For(MethodHandler mh) {
		this.mh = mh;
		this.mh.visitLabel(start);
	}

	public void exit(int op) {
		this.mh.visitJumpInsn(op, stop);
	}

	@Override
	public void close() {
		this.mh.visitJumpInsn(GOTO, start);
		this.mh.visitLabel(stop);
	}

	public static For create(MethodHandler mh) {
		return new For(mh);
	}
}
