package dev.quantumfusion.hyphen.codegen.statement;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.Variable;

import static org.objectweb.asm.Opcodes.*;

public class ArrayFor extends For implements AutoCloseable {
	private final Variable i;
	private final Runnable arrayLoad;
	private final Runnable getterFunc;

	protected ArrayFor(MethodHandler mh, Runnable arrayLoad, Variable i, Runnable getterFunc) {
		super(mh);
		this.i = i;
		this.arrayLoad = arrayLoad;
		this.getterFunc = getterFunc;
	}

	public static ArrayFor createArray(MethodHandler mh, Variable array, Variable i, Variable length) {
		return create(mh, () -> mh.varOp(ILOAD, array), i, length, () -> mh.op(AALOAD), () -> mh.op(ARRAYLENGTH));
	}

	public static ArrayFor create(MethodHandler mh, Runnable arrayLoad, Variable i, Variable length, Runnable getterFunc, Runnable lengthFunc) {
		if (i == null) {
			i = mh.addVar("i", int.class);
			mh.op(ICONST_0);
			mh.varOp(ISTORE, i);
		}
		if (length == null) {
			length = mh.addVar("length", int.class);
			arrayLoad.run();
			lengthFunc.run();
			mh.varOp(ISTORE, length);
		}
		final ArrayFor array1 = new ArrayFor(mh, arrayLoad, i, getterFunc);
		mh.varOp(ILOAD, i, length);
		array1.exit(IF_ICMPGE);
		return array1;
	}

	public void getElement() {
		arrayLoad.run();
		mh.varOp(ILOAD, i);
		getterFunc.run();
	}

	@Override
	public void close() {
		mh.visitIincInsn(i.pos(), 1); // i++
		super.close();
	}
}
