package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.Variable;
import dev.quantumfusion.hyphen.scan.annotations.DataFixedArraySize;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.util.GenUtil;

import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public abstract class IndexedDef extends MethodDef {
	protected final SerializerDef componentDef;
	protected final Clazz component;
	protected final Consumer<MethodHandler> getterFunc;
	protected final Consumer<MethodHandler> lengthFunc;
	private final Integer fixedSize;

	public IndexedDef(SerializerHandler<?, ?> handler, Clazz clazz, Clazz component, Consumer<MethodHandler> getterFunc, Consumer<MethodHandler> lengthFunc) {
		super(handler, clazz);
		this.component = component;
		this.getterFunc = getterFunc;
		this.fixedSize = (Integer) clazz.getAnnotationValue(DataFixedArraySize.class);
		this.lengthFunc = lengthFunc;
		this.componentDef = handler.acquireDef(component);
	}

	@Override
	public void scan(SerializerHandler<?, ?> handler, Clazz clazz) {

	}

	public abstract void writeGetConverter(MethodHandler mh);

	@Override
	protected void writeMethodGet(MethodHandler mh) {
		final Variable length = mh.addVar("length", int.class);

		if (fixedSize == null) {
			mh.loadIO();
			mh.getIO(int.class);
			mh.op(DUP);
			mh.varOp(ISTORE, length);
		} else mh.visitLdcInsn(fixedSize);
		mh.typeOp(ANEWARRAY, component.getBytecodeClass());
		loopArray(mh, length, (i) -> {
			mh.op(DUP);
			mh.varOp(ILOAD, i);
			componentDef.writeGet(mh);
			mh.op(AASTORE);
		});

		writeGetConverter(mh);
	}

	@Override
	protected void writeMethodPut(MethodHandler mh, Runnable valueLoad) {
		final Variable length = mh.addVar("length", int.class);

		if (fixedSize == null) {
			mh.loadIO();
			valueLoad.run();
			lengthFunc.accept(mh);
			mh.op(DUP);
			mh.varOp(ISTORE, length);
			mh.putIO(int.class);
		}

		loopArray(mh, length, (i) -> componentDef.writePut(mh, () -> loadArrayValue(mh, valueLoad, i)));
	}

	@Override
	public int getStaticSize() {
		return this.fixedSize == null ? 4 : this.fixedSize * this.componentDef.getStaticSize();
	}

	@Override
	public boolean hasDynamicSize() {
		return this.fixedSize == null || this.componentDef.hasDynamicSize();
	}

	@Override
	protected void writeMethodMeasure(MethodHandler mh, Runnable valueLoad) {
		if (this.fixedSize == null) {
			int componentSize = this.componentDef.getStaticSize();

			if (componentSize != 0) {
				// TODO: consider sing shifting if component size is a pot
				mh.visitLdcInsn(componentSize);
				valueLoad.run();
				this.lengthFunc.accept(mh);
				mh.op(IMUL);
			} else mh.op(ICONST_0);
		} else mh.op(ICONST_0);

		if (componentDef.hasDynamicSize()) {
			final Variable length = mh.addVar("length", int.class);
			if (fixedSize == null) {
				valueLoad.run();
				lengthFunc.accept(mh);
				mh.varOp(ISTORE, length);
			}
			loopArray(mh, length, (i) -> {
				componentDef.writeMeasure(mh, () -> loadArrayValue(mh, valueLoad, i));
				mh.op(IADD);
			});
		}
	}

	private void loadArrayValue(MethodHandler mh, Runnable valueLoad, Variable i) {
		valueLoad.run();
		mh.varOp(ILOAD, i);
		getterFunc.accept(mh);
		GenUtil.shouldCastGeneric(mh, component);
	}

	public void loopArray(MethodHandler mh, Variable length, Consumer<Variable> value) {
		final Variable i = mh.addVar("i", int.class, ICONST_0);
		var top = mh.defineLabel();
		mh.varOp(ILOAD, i);
		if (fixedSize == null) mh.varOp(ILOAD, length);
		else mh.visitLdcInsn(fixedSize);
		var end = mh.jump(IF_ICMPGE);

		value.accept(i);

		mh.inc(i, 1);
		mh.jump(GOTO, top);
		mh.defineLabel(end);
	}
}
