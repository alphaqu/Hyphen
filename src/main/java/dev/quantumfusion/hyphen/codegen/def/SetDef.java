package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.statement.While;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.scan.type.ParaClazz;
import dev.quantumfusion.hyphen.util.GenUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

public class SetDef extends MethodDef {
	private Clazz key;
	private SerializerDef keyDef;

	public SetDef(SerializerHandler<?, ?> handler, ParaClazz clazz) {
		super(handler, clazz);
	}

	@Override
	public void scan(SerializerHandler<?, ?> handler, Clazz clazz) {
		this.key = clazz.define("E");
		this.keyDef = handler.acquireDef(key);
	}

	@Override
	protected void writeMethodPut(MethodHandler mh, Runnable valueLoad) {
		valueLoad.run();
		mh.loadIO();
		mh.op(DUP2, SWAP);
		mh.callInst(INVOKEINTERFACE, Set.class, "size", int.class);
		mh.putIO(int.class);

		var iterator = mh.addVar("iterator", Iterator.class);
		var entry = mh.addVar("value", Object.class);

		valueLoad.run();
		mh.callInst(INVOKEINTERFACE, Iterable.class, "iterator", Iterator.class);
		mh.varOp(ISTORE, iterator);

		try (While aWhile = While.create(mh)) {
			mh.varOp(ILOAD, iterator);
			mh.callInst(INVOKEINTERFACE, Iterator.class, "hasNext", boolean.class);

			aWhile.exit(IFEQ); // exit if false

			mh.varOp(ILOAD, iterator);
			mh.callInst(INVOKEINTERFACE, Iterator.class, "next", Object.class);
			GenUtil.ensureCasted(mh, this.key.getDefinedClass(), Object.class);
			mh.varOp(ISTORE, entry);

			this.keyDef.writePut(mh, () -> mh.varOp(ILOAD, entry));
		}
	}

	@Override
	protected void writeMethodGet(MethodHandler mh) {
		var length = mh.addVar("length", int.class);
		var i = mh.addVar("i", int.class);

		mh.typeOp(NEW, HashSet.class);
		mh.op(DUP);
		mh.loadIO();
		mh.getIO(int.class);
		mh.op(DUP);
		mh.varOp(ISTORE, length);
		mh.callInst(INVOKESPECIAL, HashSet.class, "<init>", Void.TYPE, int.class);

		mh.op(ICONST_0);
		mh.varOp(ISTORE, i);

		try (var anFor = While.create(mh)) {
			mh.varOp(ILOAD, i, length);
			anFor.exit(IF_ICMPGE);

			mh.op(DUP);
			this.keyDef.writeGet(mh);
			mh.callInst(INVOKEVIRTUAL, HashSet.class, "add", boolean.class, Object.class);
			mh.op(POP);

			mh.visitIincInsn(i.pos(), 1);
		}
	}

	@Override
	protected void writeMethodMeasure(MethodHandler mh, Runnable valueLoad) {
		boolean hasDynamic = this.keyDef.hasDynamicSize();

		long staticSize = this.keyDef.getStaticSize();
		if (staticSize == 0) {
			mh.op(ICONST_0);
		} else {
			valueLoad.run();
			mh.callInst(INVOKEINTERFACE, Set.class, "size", int.class);
			mh.visitLdcInsn(staticSize);
			mh.op(IMUL);
		}

		if (hasDynamic) {
			var iterator = mh.addVar("iterator", Iterator.class);
			var entry = mh.addVar("value", Object.class);

			valueLoad.run();
			mh.callInst(INVOKEINTERFACE, Iterable.class, "iterator", Iterator.class);
			mh.varOp(ISTORE, iterator);

			try (While aWhile = While.create(mh)) {
				mh.varOp(ILOAD, iterator);
				mh.callInst(INVOKEINTERFACE, Iterator.class, "hasNext", boolean.class);
				aWhile.exit(IFEQ); // exit if false

				mh.varOp(ILOAD, iterator);
				mh.callInst(INVOKEINTERFACE, Iterator.class, "next", Object.class);
				GenUtil.ensureCasted(mh, this.key.getDefinedClass(), Object.class);
				mh.varOp(ISTORE, entry);
				this.keyDef.writeMeasure(mh, () -> mh.varOp(ILOAD, entry));
				mh.op(LADD);
			}
		}
	}

	@Override
	public long getStaticSize() {
		return 4;
	}
}
