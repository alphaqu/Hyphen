package dev.quantumfusion.hyphen.codegen.statement;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.Variable;

import static org.objectweb.asm.Opcodes.*;

public class ArrayFor extends For implements AutoCloseable {
	private final Variable i;
	private final Variable array;

	protected ArrayFor(MethodHandler mh, Variable array, Variable i, Variable length) {
		super(mh);
		this.i = i;
		this.array = array;
		this.mh.varOp(ILOAD, i, length);
		exit(IF_ICMPGE);
	}

	public void getElement() {
		mh.varOp(ILOAD, array, i);
		mh.op(AALOAD);
	}

	@Override
	public void close() {
		mh.visitIincInsn(i.pos(), 1); // i++
		super.close();
	}

	public static ArrayFor create(MethodHandler mh, Variable array, Variable i, Variable length) {
		if (i == null) {
			i = mh.addVar("i", int.class);
			mh.op(ICONST_0);
			mh.varOp(ISTORE, i);
		}
		if (length == null) {
			length = mh.addVar("length", int.class);
			mh.varOp(ILOAD, array);
			mh.op(ARRAYLENGTH);
			mh.varOp(ISTORE, length);
		}
		return new ArrayFor(mh, array, i, length);
	}
}
