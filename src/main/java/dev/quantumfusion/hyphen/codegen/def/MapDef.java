package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerGenerator;
import dev.quantumfusion.hyphen.codegen.MethodWriter;
import dev.quantumfusion.hyphen.codegen.statement.While;
import dev.quantumfusion.hyphen.scan.struct.ClassStruct;
import dev.quantumfusion.hyphen.scan.struct.Struct;
import dev.quantumfusion.hyphen.util.GenUtil;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class MapDef extends MethodDef<ClassStruct> {
	private Struct keyStruct;
	private Struct valueStruct;
	private SerializerDef keyDef;
	private SerializerDef valueDef;

	public MapDef(Struct clazz) {
		super((ClassStruct) clazz);
	}

	@Override
	public void scan(SerializerGenerator<?, ?> handler) {
		super.scan(handler);
		this.keyStruct = struct.getParameter("K");
		this.valueStruct = struct.getParameter("V");
		this.keyDef = handler.acquireDef(this.keyStruct);
		this.valueDef = handler.acquireDef(this.valueStruct);
		//this.putLambdaMethod = handler.codegenHandler.createMethodInfo(clazz, "$lambda$put", Void.TYPE, handler.ioClass, this.keyClazz.getBytecodeClass(), this.valueClazz.getBytecodeClass());
	}

	@Override
	protected void writeMethodPut(MethodWriter mh, Runnable valueLoad) {
		valueLoad.run();
		mh.loadIO();
		mh.op(DUP2, SWAP);
		mh.callInst(INVOKEINTERFACE, Map.class, "size", int.class);
		mh.putIO(int.class);

		// add dynamic sizes
		var iterator = mh.addVar("iterator", Iterator.class);
		var entry = mh.addVar("value", Object.class);

		// get iterator
		valueLoad.run();
		mh.callInst(INVOKEINTERFACE, Map.class, "entrySet", Set.class);
		mh.callInst(INVOKEINTERFACE, Iterable.class, "iterator", Iterator.class);
		mh.varOp(ISTORE, iterator);

		try (While aWhile = While.create(mh)) {
			mh.varOp(ILOAD, iterator);
			mh.callInst(INVOKEINTERFACE, Iterator.class, "hasNext", boolean.class);

			aWhile.exit(IFEQ); // exit if false

			mh.varOp(ILOAD, iterator);
			mh.callInst(INVOKEINTERFACE, Iterator.class, "next", Object.class);
			mh.typeOp(CHECKCAST, Map.Entry.class);
			mh.varOp(ISTORE, entry);

			this.keyDef.writePut(mh, () -> {
				mh.varOp(ILOAD, entry);
				mh.callInst(INVOKEINTERFACE, Map.Entry.class, "getKey", Object.class);
				GenUtil.ensureCasted(mh, this.keyStruct, Object.class);
			});
			this.valueDef.writePut(mh, () -> {
				mh.varOp(ILOAD, entry);
				mh.callInst(INVOKEINTERFACE, Map.Entry.class, "getValue", Object.class);
				GenUtil.ensureCasted(mh, this.valueStruct, Object.class);
			});
		}
	}

	@Override
	protected void writeMethodGet(MethodWriter mh) {
		var length = mh.addVar("length", int.class);
		var i = mh.addVar("i", int.class);

		mh.typeOp(NEW, HashMap.class);
		mh.op(DUP);
		mh.loadIO();
		mh.getIO(int.class);
		mh.op(DUP);
		mh.varOp(ISTORE, length);
		mh.callInst(INVOKESPECIAL, HashMap.class, "<init>", Void.TYPE, int.class);

		mh.op(ICONST_0);
		mh.varOp(ISTORE, i);

		try (var anFor = While.create(mh)) {
			mh.varOp(ILOAD, i, length);
			anFor.exit(IF_ICMPGE);

			mh.op(DUP);
			this.keyDef.writeGet(mh);
			this.valueDef.writeGet(mh);
			mh.callInst(INVOKEVIRTUAL, HashMap.class, "put", Object.class, Object.class, Object.class);
			mh.op(POP);

			mh.visitIincInsn(i.pos(), 1);
		}
	}

	@Override
	protected void writeMethodMeasure(MethodWriter mh, Runnable valueLoad) {
		int x = (this.keyDef.hasDynamicSize() ? 1 : 0) | (this.valueDef.hasDynamicSize() ? 2 : 0);

		long staticSize = this.keyDef.getStaticSize() + this.valueDef.getStaticSize();
		if (staticSize == 0) {
			mh.op(LCONST_0);
		} else {
			valueLoad.run();
			mh.callInst(INVOKEINTERFACE, Map.class, "size", int.class);
			mh.op(I2L);
			mh.visitLdcInsn(staticSize);
			mh.op(LMUL);
		}

		if (x > 0) {
			// add dynamic sizes
			var iterator = mh.addVar("iterator", Iterator.class);
			var entry = mh.addVar("value", Object.class);

			// get iterator
			valueLoad.run();
			if (x == 1) {
				mh.callInst(INVOKEINTERFACE, Map.class, "keySet", Set.class);
			}
			if (x == 2) {
				mh.callInst(INVOKEINTERFACE, Map.class, "values", Collection.class);
			}
			if (x == 3) {
				mh.callInst(INVOKEINTERFACE, Map.class, "entrySet", Set.class);
			}
			mh.callInst(INVOKEINTERFACE, Iterable.class, "iterator", Iterator.class);
			mh.varOp(ISTORE, iterator);

			try (While aWhile = While.create(mh)) {
				mh.varOp(ILOAD, iterator);
				mh.callInst(INVOKEINTERFACE, Iterator.class, "hasNext", boolean.class);

				aWhile.exit(IFEQ); // exit if false

				mh.varOp(ILOAD, iterator);
				mh.callInst(INVOKEINTERFACE, Iterator.class, "next", Object.class);
				if (x == 3) {
					mh.typeOp(CHECKCAST, Map.Entry.class);
				}
				mh.varOp(ISTORE, entry);

				if (x == 1) {
					this.keyDef.writeMeasure(mh, () -> {
						mh.varOp(ILOAD, entry);
						GenUtil.ensureCasted(mh, this.keyStruct, Object.class);
					});
				}
				if (x == 2) {
					this.valueDef.writeMeasure(mh, () -> {
						mh.varOp(ILOAD, entry);
						GenUtil.ensureCasted(mh, this.valueStruct, Object.class);
					});
				}
				if (x == 3) {
					this.keyDef.writeMeasure(mh, () -> {
						mh.varOp(ILOAD, entry);
						mh.callInst(INVOKEINTERFACE, Map.Entry.class, "getKey", Object.class);
						GenUtil.ensureCasted(mh, this.keyStruct, Object.class);
					});
					this.valueDef.writeMeasure(mh, () -> {
						mh.varOp(ILOAD, entry);
						mh.callInst(INVOKEINTERFACE, Map.Entry.class, "getValue", Object.class);
						GenUtil.ensureCasted(mh, this.valueStruct, Object.class);
					});
					mh.op(LADD);
				}
				mh.op(LADD);
			}
		}
	}

	@Override
	public long getStaticSize() {
		return 4; // size of the map
	}
}