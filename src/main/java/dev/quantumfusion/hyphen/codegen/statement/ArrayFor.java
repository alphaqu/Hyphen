package dev.quantumfusion.hyphen.codegen.statement;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.Variable;

import static org.objectweb.asm.Opcodes.*;

public class ArrayFor extends For implements AutoCloseable {
	private final Variable i;
	private final Variable array;
	private final Variable length;

	public ArrayFor(MethodHandler mh, String array) {
		this(mh, array, "i", "length");
	}

	public ArrayFor(MethodHandler mh, String array, String iName, String lengthName) {
		this(mh, array, iName, mh.addVar(lengthName, int.class));
		mh.varOp(ILOAD, array);
		mh.op(ARRAYLENGTH);
		mh.varOp(ISTORE, lengthName);
	}

	public ArrayFor(MethodHandler mh, String array, String iName, Variable length) {
		super(mh);
		this.i = mh.addVar(iName, int.class);
		this.array = mh.getVar(array);
		this.length = length;

		mh.op(ICONST_0);
		mh.varOp(ISTORE, this.i);
	}

	public ArrayFor start() {
		super.start();
		mh.varOp(ILOAD, this.i, length);
		exit(IF_ICMPGE);
		return this;
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
}
