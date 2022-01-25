package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodInfo;
import dev.quantumfusion.hyphen.codegen.statement.While;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.scan.type.ParaClazz;
import dev.quantumfusion.hyphen.util.GenUtil;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class MapDef extends MethodDef {
	private Clazz keyClazz;
	private Clazz valueClazz;
	private SerializerDef keyDef;
	private SerializerDef valueDef;
	private MethodInfo putLambdaMethod;


	public MapDef(SerializerHandler<?, ?> handler, ParaClazz clazz) {
		super(handler, clazz);
	}

	@Override
	public void scan(SerializerHandler<?, ?> handler, Clazz clazz) {
		this.keyClazz = clazz.define("K");
		this.valueClazz = clazz.define("V");
		this.keyDef = handler.acquireDef(this.keyClazz);
		this.valueDef = handler.acquireDef(this.valueClazz);
		//this.putLambdaMethod = handler.codegenHandler.createMethodInfo(clazz, "$lambda$put", Void.TYPE, handler.ioClass, this.keyClazz.getBytecodeClass(), this.valueClazz.getBytecodeClass());
	}

	@Override
	protected void writeMethodPut(MethodHandler mh, Runnable valueLoad) {
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
				GenUtil.shouldCastGeneric(mh, this.keyClazz.getDefinedClass(), Object.class);
			});
			this.valueDef.writePut(mh, () -> {
				mh.varOp(ILOAD, entry);
				mh.callInst(INVOKEINTERFACE, Map.Entry.class, "getValue", Object.class);
				GenUtil.shouldCastGeneric(mh, this.valueClazz.getDefinedClass(), Object.class);
			});
		}
	}

	@Override
	protected void writeMethodGet(MethodHandler mh) {
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
	protected void writeMethodMeasure(MethodHandler mh, Runnable valueLoad) {
		int x = (this.keyDef.hasDynamicSize() ? 1 : 0) | (this.valueDef.hasDynamicSize() ? 2 : 0);

		int staticSize = this.keyDef.getStaticSize() + this.valueDef.getStaticSize();
		if (staticSize == 0)
			mh.op(ICONST_0);
		else {
			valueLoad.run();
			mh.callInst(INVOKEINTERFACE, Map.class, "size", int.class);
			mh.visitLdcInsn(staticSize);
			mh.op(IMUL);
		}

		if (x > 0) {
			// add dynamic sizes
			var iterator = mh.addVar("iterator", Iterator.class);
			var entry = mh.addVar("value", Object.class);

			// get iterator
			valueLoad.run();
			if (x == 1) mh.callInst(INVOKEINTERFACE, Map.class, "keySet", Set.class);
			if (x == 2) mh.callInst(INVOKEINTERFACE, Map.class, "values", Collection.class);
			if (x == 3) mh.callInst(INVOKEINTERFACE, Map.class, "entrySet", Set.class);
			mh.callInst(INVOKEINTERFACE, Iterable.class, "iterator", Iterator.class);
			mh.varOp(ISTORE, iterator);

			try (While aWhile = While.create(mh)) {
				mh.varOp(ILOAD, iterator);
				mh.callInst(INVOKEINTERFACE, Iterator.class, "hasNext", boolean.class);

				aWhile.exit(IFEQ); // exit if false

				mh.varOp(ILOAD, iterator);
				mh.callInst(INVOKEINTERFACE, Iterator.class, "next", Object.class);
				if (x == 3) mh.typeOp(CHECKCAST, Map.Entry.class);
				mh.varOp(ISTORE, entry);

				if (x == 1) this.keyDef.writeMeasure(mh, () -> {
					mh.varOp(ILOAD, entry);
					GenUtil.shouldCastGeneric(mh, this.keyClazz.getDefinedClass(), Object.class);
				});
				if (x == 2) this.valueDef.writeMeasure(mh, () -> {
					mh.varOp(ILOAD, entry);
					GenUtil.shouldCastGeneric(mh, this.valueClazz.getDefinedClass(), Object.class);
				});
				if (x == 3) {
					this.keyDef.writeMeasure(mh, () -> {
						mh.varOp(ILOAD, entry);
						mh.callInst(INVOKEINTERFACE, Map.Entry.class, "getKey", Object.class);
						GenUtil.shouldCastGeneric(mh, this.keyClazz.getDefinedClass(), Object.class);
					});
					this.valueDef.writeMeasure(mh, () -> {
						mh.varOp(ILOAD, entry);
						mh.callInst(INVOKEINTERFACE, Map.Entry.class, "getValue", Object.class);
						GenUtil.shouldCastGeneric(mh, this.valueClazz.getDefinedClass(), Object.class);
					});
					mh.op(IADD);
				}
				mh.op(IADD);
			}
		}
	}

	@Override
	public int getStaticSize() {
		return 4; // size of the map
	}

	@Override
	public void writeMethods(CodegenHandler<?, ?> handler, CodegenHandler.MethodWriter writer, boolean spark) {
		super.writeMethods(handler, writer, spark);
		//if (!handler.options.get(Options.DISABLE_MEASURE))
		//	writer.writeMethod(this.clazz, this.putLambdaMethod, false, true,
		//					   mh -> {
		//						   this.keyDef.writePut(mh, () -> mh.parameterOp(ILOAD, 1));
		//						   this.valueDef.writePut(mh, () -> mh.parameterOp(ILOAD, 2));
		//					   });
	}
}