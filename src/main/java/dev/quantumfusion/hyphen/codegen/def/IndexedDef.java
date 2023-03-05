package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerGenerator;
import dev.quantumfusion.hyphen.codegen.MethodWriter;
import dev.quantumfusion.hyphen.codegen.Variable;
import dev.quantumfusion.hyphen.codegen.statement.If;
import dev.quantumfusion.hyphen.codegen.statement.IfElse;
import dev.quantumfusion.hyphen.scan.annotations.DataFixedArraySize;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.struct.Struct;
import dev.quantumfusion.hyphen.util.GenUtil;

import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public abstract class IndexedDef<D extends Struct> extends MethodDef<D> {
	protected SerializerDef componentDef;
	protected Struct component;
	protected boolean componentNullable;
	private final Integer fixedSize;

	public IndexedDef(String name, D clazz) {
		super(clazz, name);
		DataFixedArraySize annotation = clazz.getAnnotation(DataFixedArraySize.class);
		if (annotation != null) {
			this.fixedSize = annotation.value();
		} else {
			this.fixedSize = null;
		}
	}

	@Override
	public void scan(SerializerGenerator<?, ?> handler) {
		this.component = scanComponent(handler);
		this.componentDef = handler.acquireDef(component);
		this.componentNullable = component.isAnnotationPresent(DataNullable.class);
		super.scan(handler);
	}

	public abstract Struct scanComponent(SerializerGenerator<?, ?> handler);
	public abstract void writeGetElement(MethodWriter mh);
	public abstract void writeLength(MethodWriter mh);

	public abstract void writeGetConverter(MethodWriter mh);

	@Override
	protected void writeMethodGet(MethodWriter mh) {
		final Variable length = mh.addVar("length", int.class);

		if (fixedSize == null) {
			mh.loadIO();
			mh.getIO(int.class);
			mh.op(DUP);
			mh.varOp(ISTORE, length);
		} else {
			mh.visitLdcInsn(fixedSize);
		}
		mh.typeOp(ANEWARRAY, component.getBytecodeClass());
		loopArray(mh, length, (i) -> {
			mh.op(DUP);
			if (componentNullable) {
				mh.loadIO();
				mh.getIO(byte.class);
				try (var anIf = new IfElse(mh, IFEQ)) {
					mh.varOp(ILOAD, i);
					componentDef.writeGet(mh);
					mh.op(AASTORE);
					anIf.elseEnd();
					mh.op(POP);
				}
			} else {
				mh.varOp(ILOAD, i);
				componentDef.writeGet(mh);
				mh.op(AASTORE);
			}
		});

		writeGetConverter(mh);
	}

	@Override
	protected void writeMethodPut(MethodWriter mh, Runnable valueLoad) {
		final Variable length = mh.addVar("length", int.class);

		if (fixedSize == null) {
			mh.loadIO();
			valueLoad.run();
			writeLength(mh);
			mh.op(DUP);
			mh.varOp(ISTORE, length);
			mh.putIO(int.class);
		}

		loopArray(mh, length, (i) -> {
			Variable entryTemp = mh.addVar("entry", Object.class);
			loadArrayValue(mh, valueLoad, i);
			mh.varOp(ISTORE, entryTemp);

			if (componentNullable) {
				mh.varOp(ILOAD, entryTemp);
				try (var anIf = new IfElse(mh, IFNONNULL)) {
					mh.loadIO();
					mh.op(ICONST_0);
					mh.putIO(byte.class);
					anIf.elseEnd();

					mh.loadIO();
					mh.op(ICONST_1);
					mh.putIO(byte.class);
					componentDef.writePut(mh, () -> mh.varOp(ILOAD, entryTemp));
				}
			} else {
				componentDef.writePut(mh, () -> mh.varOp(ILOAD, entryTemp));
			}
		});
	}

	@Override
	public long getStaticSize() {
		if (this.fixedSize == null) {
			return 4;
		} else if (!this.componentNullable) {
			return this.fixedSize * this.componentDef.getStaticSize();
		} else {
			return 0;
		}
	}

	@Override
	public boolean hasDynamicSize() {
		return this.fixedSize == null || this.componentDef.hasDynamicSize() || componentNullable;
	}

	@Override
	protected void writeMethodMeasure(MethodWriter mh, Runnable valueLoad) {
		if (this.fixedSize == null && !componentNullable) {
			long componentSize = this.componentDef.getStaticSize();

			if (componentSize != 0) {
				// TODO: consider sing shifting if component size is a pot
				mh.visitLdcInsn(componentSize);
				valueLoad.run();
				writeLength(mh);
				mh.op(I2L);
				mh.op(LMUL);
			} else {
				mh.op(LCONST_0);
			}
		} else {
			mh.op(LCONST_0);
		}

		if (componentDef.hasDynamicSize() || componentNullable) {
			final Variable length = mh.addVar("length", int.class);
			if (fixedSize == null) {
				valueLoad.run();
				writeLength(mh);
				mh.varOp(ISTORE, length);
			}
			loopArray(mh, length, (i) -> {
				Variable entryTemp = mh.addVar("entry", Object.class);
				loadArrayValue(mh, valueLoad, i);
				mh.varOp(ISTORE, entryTemp);

				if (componentNullable) {
					mh.op(LCONST_1);
					mh.varOp(ILOAD, entryTemp);
					try (var anIf = new If(mh, IFNULL)) {
						componentDef.writeMeasure(mh, () -> loadArrayValue(mh, valueLoad, i));
						mh.op(LADD);
					}

				} else {
					componentDef.writeMeasure(mh, () -> loadArrayValue(mh, valueLoad, i));
				}
				mh.op(LADD);
			});
		}
	}

	private void loadArrayValue(MethodWriter mh, Runnable valueLoad, Variable i) {
		valueLoad.run();
		mh.varOp(ILOAD, i);
		writeGetElement(mh);
		GenUtil.ensureCasted(mh, component);
	}

	public void loopArray(MethodWriter mh, Variable length, Consumer<Variable> value) {
		final Variable i = mh.addVar("i", int.class, ICONST_0);
		var top = mh.defineLabel();
		mh.varOp(ILOAD, i);
		if (fixedSize == null) {
			mh.varOp(ILOAD, length);
		} else {
			mh.visitLdcInsn(fixedSize);
		}
		var end = mh.jump(IF_ICMPGE);

		value.accept(i);

		mh.inc(i, 1);
		mh.jump(GOTO, top);
		mh.defineLabel(end);
	}
}
