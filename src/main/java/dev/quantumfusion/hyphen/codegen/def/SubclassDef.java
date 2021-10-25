package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
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
	private final SerializerDef[] subDefs;
	private final boolean allSameStaticSize;

	public SubclassDef(SerializerHandler<?, ?> handler, Clazz clazz, Class<?>[] subClasses) {
		super(handler, clazz, "SUB{ # " + Arrays.stream(subClasses).map(Class::getSimpleName).collect(Collectors.joining(", ")) + "}");
		this.subClasses = subClasses;
		this.subDefs = ArrayUtil.map(subClasses, SerializerDef[]::new, subclass -> handler.acquireDef(clazz.asSub(subclass)));

		int size = this.subDefs[0].getStaticSize();

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
		mh.varOp(ILOAD, "io");
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
		mh.addVar("clz", Class.class);
		valueLoad.run();
		mh.callInst(INVOKEVIRTUAL, Object.class, "getClass", Class.class);
		mh.varOp(ISTORE, "clz");
		ArrayUtil.dualForEach(this.subClasses, this.subDefs, (clz, serializerDef, i) -> {
			mh.varOp(ILOAD, "clz");
			mh.visitLdcInsn(Type.getType(clz));
			try (var f = new If(mh, IF_ACMPNE)) {
				mh.varOp(ILOAD, "io");
				mh.visitLdcInsn(i);
				mh.putIO(byte.class);

				serializerDef.writePut(mh, () -> {
					valueLoad.run();
					GenUtil.shouldCastGeneric(mh, clz, this.clazz.getBytecodeClass());
				});
				mh.op(RETURN);
			}
		});
		// TODO: throw
	}

	@Override
	protected void writeMethodMeasure(MethodHandler mh, Runnable valueLoad) {
		mh.addVar("clz", Class.class);
		valueLoad.run();
		mh.callInst(INVOKEVIRTUAL, Object.class, "getClass", Class.class);
		mh.varOp(ISTORE, "clz");

		if (this.hasDynamicSize()) {
			ArrayUtil.dualFor(this.subClasses, this.subDefs, (clz, serializerDef) -> {
				mh.varOp(ILOAD, "clz");
				mh.visitLdcInsn(Type.getType(clz));
				try (var anIf = new If(mh, IF_ACMPNE)) {
					if (serializerDef.hasDynamicSize())
						serializerDef.writeMeasure(mh, () -> {
							valueLoad.run();
							GenUtil.shouldCastGeneric(mh, clz, this.clazz.getBytecodeClass());
						});
					int ss = serializerDef.getStaticSize();
					if (!this.allSameStaticSize && ss != 0) {
						mh.visitLdcInsn(ss);
						if (serializerDef.hasDynamicSize()) mh.op(IADD);
					} else if (!serializerDef.hasDynamicSize())
						mh.op(ICONST_0);

					mh.op(IRETURN);
				}
			});
			// TODO: throw
			mh.op(ICONST_0);
		}
	}

	@Override
	public int getStaticSize() {
		if (this.subDefs.length == 0) // ???
			return 0;
		if (this.allSameStaticSize)
			return this.subDefs[0].getStaticSize() + 1;
		return 1;
	}

	@Override
	public boolean hasDynamicSize() {
		if (this.subDefs.length == 0) // ???
			return false;


		if (!this.allSameStaticSize)
			return true;

		for (SerializerDef subDef : this.subDefs) {
			if (subDef.hasDynamicSize())
				return true;
		}

		return false;
	}
}
