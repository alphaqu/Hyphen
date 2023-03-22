package dev.notalpha.hyphen.codegen.statement;

import dev.notalpha.hyphen.codegen.MethodWriter;
import org.objectweb.asm.Label;

public class If implements AutoCloseable {
	protected final MethodWriter mh;
	protected final Label next = new Label();

	public If(MethodWriter mh, int op) {
		this.mh = mh;
		mh.visitJumpInsn(op, next);
	}

	@Override
	public void close() {
		mh.visitLabel(next);
	}
}
