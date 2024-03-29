package dev.notalpha.hyphen.codegen.def;

import dev.notalpha.hyphen.SerializerGenerator;
import dev.notalpha.hyphen.codegen.MethodWriter;
import dev.notalpha.hyphen.codegen.statement.While;
import dev.notalpha.hyphen.scan.struct.ClassStruct;
import dev.notalpha.hyphen.scan.struct.Struct;
import dev.notalpha.hyphen.util.GenUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

public class SetDef extends MethodDef<ClassStruct> {
	private Struct key;
	private SerializerDef keyDef;

	public SetDef(Struct clazz) {
		super((ClassStruct) clazz);
	}

	@Override
	public void scan(SerializerGenerator<?, ?> handler) {
		super.scan(handler);
		this.key = struct.getParameter("E");
		this.keyDef = handler.acquireDef(key);
	}

	@Override
	protected void writeMethodPut(MethodWriter mh, Runnable valueLoad) {
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
			GenUtil.ensureCasted(mh, this.key.getValueClass(), Object.class);
			mh.varOp(ISTORE, entry);

			this.keyDef.writePut(mh, () -> mh.varOp(ILOAD, entry));
		}
	}

	@Override
	protected void writeMethodGet(MethodWriter mh) {
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
	protected void writeMethodMeasure(MethodWriter mh, Runnable valueLoad) {
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
				GenUtil.ensureCasted(mh, this.key.getValueClass(), Object.class);
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
