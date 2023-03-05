package dev.quantumfusion.hyphen.codegen.statement;

import dev.quantumfusion.hyphen.codegen.MethodWriter;
import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.GOTO;

public class IfElse implements AutoCloseable {
	protected final MethodWriter mh;
	protected final Label elseLabel = new Label();
	protected final Label endLabel = new Label();


	public IfElse(MethodWriter mh, int op) {
		this.mh = mh;
		mh.visitJumpInsn(op, elseLabel);
	}


	public void elseEnd() {
		mh.visitJumpInsn(GOTO, endLabel);
		mh.visitLabel(elseLabel);
	}

	public void close() {
		mh.visitLabel(endLabel);
	}
}
