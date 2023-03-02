package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.SerializerGenerator;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.Variable;
import dev.quantumfusion.hyphen.codegen.statement.If;
import dev.quantumfusion.hyphen.codegen.statement.TableSwitch;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

public final class SubclassDef extends MethodDef {
	private final Class<?>[] subClasses;
	private SerializerDef[] subDefs;
	private boolean allSameStaticSize;

	public SubclassDef(Clazz clazz, Class<?>[] subClasses) {
		super(clazz, "SUB{ # " + Arrays.stream(subClasses).map(Class::getSimpleName).collect(Collectors.joining(", ")) + "}");
		this.subClasses = subClasses;
	}

	public void scan(SerializerGenerator<?, ?> handler) {
		this.subDefs = ArrayUtil.map(subClasses, SerializerDef[]::new, subclass -> handler.acquireDef(clazz.asSub(handler, subclass)));
		super.scan(handler);
		long size = this.subDefs[0].getStaticSize();

		for (int i = 1; i < this.subDefs.length; i++) {
			if (size != this.subDefs[i].getStaticSize()) {
				this.allSameStaticSize = false;
				return;
			}
		}
		this.allSameStaticSize = true;
	}

	@Override
	protected void writeMethodGet(MethodHandler mh) {
		mh.loadIO();
		// TODO: consider using dynamic max size like enums
		mh.getIO(byte.class);
		try (var tableSwitch = new TableSwitch(mh, 0, this.subDefs.length)) {
			tableSwitch.labels(value -> {
				this.subDefs[value].writeGet(mh);
				mh.op(ARETURN);
			});

			tableSwitch.defaultLabel();
		}
		mh.op(ACONST_NULL);
	}

	@Override
	protected void writeMethodPut(MethodHandler mh, Runnable valueLoad) {
		iterClasses(mh, valueLoad, (clz, serializerDef, i) -> {
			mh.loadIO();
			mh.visitLdcInsn(i);
			mh.putIO(byte.class);

			serializerDef.writePut(mh, () -> {
				valueLoad.run();
				GenUtil.ensureCasted(mh, clz, this.clazz.getBytecodeClass());
			});
			mh.op(RETURN);
		});
		// TODO: throw
	}

	@Override
	protected void writeMethodMeasure(MethodHandler mh, Runnable valueLoad) {
		iterClasses(mh, valueLoad, (clz, serializerDef, i) -> {
			if (serializerDef.hasDynamicSize()) {
				serializerDef.writeMeasure(mh, () -> {
					valueLoad.run();
					GenUtil.ensureCasted(mh, clz, this.clazz.getBytecodeClass());
				});
			}
			long ss = serializerDef.getStaticSize();
			if (!this.allSameStaticSize && ss != 0) {
				mh.visitLdcInsn(ss);
				if (serializerDef.hasDynamicSize()) {
					mh.op(LADD);
				}
			} else if (!serializerDef.hasDynamicSize()) {
				mh.op(LCONST_0);
			}
			mh.op(LRETURN);
		});
		// TODO: throw
		mh.op(LCONST_0);
	}

	private void iterClasses(MethodHandler mh, Runnable valueLoad, ArrayUtil.DualForEach<Class<?>, SerializerDef> action) {
		final Variable clz = mh.addVar("clz", Class.class);
		valueLoad.run();
		mh.callInst(INVOKEVIRTUAL, Object.class, "getClass", Class.class);
		mh.varOp(ISTORE, clz);
		ArrayUtil.dualForEach(this.subClasses, this.subDefs, (aClass, serializerDef, i) -> {
			mh.varOp(ILOAD, clz);
			mh.visitLdcInsn(Type.getType(aClass));
			try (var f = new If(mh, IF_ACMPNE)) {
				action.apply(aClass, serializerDef, i);
			}
		});
	}

	@Override
	public long getStaticSize() {
		if (this.subDefs.length == 0) {
			return 0;
		}
		if (this.allSameStaticSize) {
			return this.subDefs[0].getStaticSize() + 1;
		}
		return 1;
	}

	@Override
	public boolean hasDynamicSize() {
		if (this.subDefs.length == 0) {
			return false;
		}
		if (!this.allSameStaticSize) {
			return true;
		}

		for (SerializerDef subDef : this.subDefs) {
			if (subDef.hasDynamicSize()) {
				return true;
			}
		}

		return false;
	}
}
