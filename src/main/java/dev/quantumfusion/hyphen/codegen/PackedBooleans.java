package dev.quantumfusion.hyphen.codegen;

import static org.objectweb.asm.Opcodes.*;

public class PackedBooleans {
	private int booleansAmount = 0;
	private int stacks = 0;


	public void countBoolean() {
		if (booleansAmount++ % 8 == 0) {
			stacks++;
		}
	}

	public void writeGet(MethodHandler mh) {
		for (int i = 0; i < stacks; i++) {
			mh.varOp(ILOAD, "io");
			mh.getIO(byte.class);
			mh.varOp(ISTORE, mh.addVar("n_" + stacks, int.class));
		}
		stacks = 0;
		booleansAmount = 0;
	}

	public void getBoolean(MethodHandler mh) {
		int pos = (booleansAmount++) % 8;
		if (pos == 0) stacks++;

		mh.varOp(ILOAD, "n_" + stacks);
		if (pos != 0) {
			mh.visitLdcInsn(pos);
			mh.op(ISHR);
		}

		mh.op(ICONST_1, IAND);
	}

	public void writePut(MethodHandler mh) {
		for (int i = 0; i < stacks; i++) {
			mh.putIO(byte.class);
		}
	}

	public void initBoolean(MethodHandler mh) {
		if (booleansAmount++ % 8 == 0) {
			stacks++;
			mh.varOp(ILOAD, "io");
		}
	}

	public void falseBoolean(MethodHandler mh) {
		if ((booleansAmount - 1) % 8 == 0) mh.op(ICONST_0);
	}

	public void trueBoolean(MethodHandler mh) {
		final int pos = (booleansAmount - 1) % 8;
		// iload boolean
		if (pos == 0) mh.op(ICONST_1);
		else {
			mh.visitLdcInsn((byte) Math.pow(2, pos));
			mh.op(IOR);
		}
	}

	public void consumeBoolean(MethodHandler mh) {
		final int pos = (booleansAmount - 1) % 8;
		// iload boolean
		mh.visitLdcInsn(pos);
		mh.op(ISHL);
		if (pos != 0) mh.op(IOR);
	}

	public int getBytes() {
		return stacks;
	}
}
