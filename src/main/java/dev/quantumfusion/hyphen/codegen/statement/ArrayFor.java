package dev.quantumfusion.hyphen.codegen.statement;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.Variable;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class ArrayFor extends For implements AutoCloseable {
	private final Variable i;
	private final Variable array;
	private final Runnable getterFunc;

	protected ArrayFor(MethodHandler mh, Variable array, Variable i, Runnable getterFunc) {
		super(mh);
		this.i = i;
		this.array = array;
		this.getterFunc = getterFunc;
	}

	public void getElement() {
		mh.varOp(ILOAD, array, i);
		getterFunc.run();
	}

	@Override
	public void close() {
		mh.visitIincInsn(i.pos(), 1); // i++
		super.close();
	}

	public static ArrayFor createArray(MethodHandler mh, Variable array, Variable i, Variable length) {
		return create(mh, array, i, length, () -> mh.op(AALOAD), () -> mh.op(ARRAYLENGTH));
	}


	public static ArrayFor create(MethodHandler mh, Variable array, Variable i, Variable length, Runnable getterFunc, Runnable lengthFunc) {
		if (i == null) {
			i = mh.addVar("i", int.class);
			mh.op(ICONST_0);
			mh.varOp(ISTORE, i);
		}
		if (length == null) {
			length = mh.addVar("length", int.class);
			mh.varOp(ILOAD, array);
			lengthFunc.run();
			mh.varOp(ISTORE, length);
		}
		final ArrayFor array1 = new ArrayFor(mh, array, i, getterFunc);
		mh.varOp(ILOAD, i, length);
		array1.exit(IF_ICMPGE);
		return array1;
	}
}
