package dev.quantumfusion.hyphen.codegen.statement;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import org.objectweb.asm.Label;

public class If implements AutoCloseable {
	protected final MethodHandler mh;
	protected final Label next = new Label();

	public If(MethodHandler mh, int op) {
		this.mh = mh;
		mh.visitJumpInsn(op, next);
	}

	@Override
	public void close() {
		mh.visitLabel(next);
	}
}
