package dev.quantumfusion.hyphen.codegen;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

/**
 * Allows us to pack single bit values into a byte
 */
public class PackedBooleans {
	private int booleansAmount = 0;
	private int stacks = 0;
	private List<Variable> stackVariables = new ArrayList<>();

	public void countBoolean() {
		if (booleansAmount++ % 8 == 0) {
			stacks++;
		}
	}

	public void writeGet(MethodWriter mh) {
		for (int i = stacks; i > 0; i--) {
			mh.loadIO();
			mh.getIO(byte.class);
			final Variable var = mh.addVar(i + "_n", int.class);
			stackVariables.add(0, var);
			mh.varOp(ISTORE, var);
		}
		stacks = -1;
		booleansAmount = 0;

	}

	public void getBoolean(MethodWriter mh) {
		int pos = (booleansAmount++) % 8;
		if (pos == 0) {
			stacks++;
		}


		mh.varOp(ILOAD, stackVariables.get(stacks));
		if (pos != 0) {
			mh.visitLdcInsn(pos);
			mh.op(IUSHR);
		}
		;

		mh.op(ICONST_1, IAND);
	}

	public void writePut(MethodWriter mh) {
		for (int i = 0; i < stacks; i++) {
			mh.putIO(byte.class);
		}
	}

	public void initBoolean(MethodWriter mh) {
		if (booleansAmount++ % 8 == 0) {
			stacks++;
			mh.loadIO();
		}
	}

	public void falseBoolean(MethodWriter mh) {
		if ((booleansAmount - 1) % 8 == 0) {
			mh.op(ICONST_0);
		}
	}

	public void trueBoolean(MethodWriter mh) {
		final int pos = (booleansAmount - 1) % 8;
		// iload boolean
		if (pos == 0) {
			mh.op(ICONST_1);
		} else {
			mh.visitLdcInsn((byte) Math.pow(2, pos));
			mh.op(IOR);
		}
	}

	public void consumeBoolean(MethodWriter mh) {
		final int pos = (booleansAmount - 1) % 8;
		// iload boolean
		mh.visitLdcInsn(pos);
		mh.op(ISHL);
		if (pos != 0) {
			mh.op(IOR);
		}
	}
}
