package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

public class SubclassDef extends MethodDef {
	private final Class<?>[] subClasses;
	private final SerializerDef[] subDefs;

	public SubclassDef(SerializerHandler<?, ?> handler, Clazz clazz, Class<?>[] subClasses) {
		super(handler.codegenHandler, clazz, "SUB{" + clazz + " # " + Arrays.stream(subClasses).map(Class::getSimpleName).collect(Collectors.joining(", ")) + "}");
		this.subClasses = subClasses;
		this.subDefs = ArrayUtil.map(subClasses, SerializerDef[]::new, subclass ->
				handler.acquireDef(clazz.asSub(subclass))
		);
	}

	@Override
	public void writeMethodGet(MethodHandler mh) {
		mh.varOp(ILOAD, "io");
		GenUtil.getIO(mh, byte.class);

		Label[] labels = ArrayUtil.map(this.subDefs, Label[]::new, i -> new Label());
		Label def = new Label();

		mh.visitTableSwitchInsn(0, labels.length - 1, def, labels);

		ArrayUtil.dualFor(labels, this.subDefs, (label, serializerDef) -> {
			mh.visitLabel(label);
			serializerDef.writeGet(mh);
			mh.op(ARETURN);
		});

		mh.visitLabel(def);
		// TODO throw error
		mh.op(ACONST_NULL, ARETURN);
	}

	@Override
	public void writeMethodPut(MethodHandler mh) {
		mh.addVar("clz", Class.class);

		mh.varOp(ILOAD, "data");
		mh.visitMethodInsn(INVOKEVIRTUAL, Object.class, "getClass", Class.class);
		mh.varOp(ISTORE, "clz");

		ArrayUtil.dualForEach(this.subClasses, this.subDefs, (clz, serializerDef, i) -> {
			mh.varOp(ILOAD, "clz");
			mh.visitLdcInsn(Type.getType(clz));

			Label next = new Label();

			mh.visitJumpInsn(IF_ACMPNE, next);
			mh.varOp(ILOAD, "io");
			mh.visitLdcInsn(i);
			GenUtil.putIO(mh, byte.class);

			serializerDef.writePut(mh, () -> {
				mh.varOp(ILOAD, "data");
				if(!clz.isAssignableFrom(this.clazz.getBytecodeClass()))
				mh.visitTypeInsn(CHECKCAST, clz);
			});
			mh.op(RETURN);

			mh.visitLabel(next);
		});

		// TODO: throw

		mh.op(RETURN);
	}

	@Override
	public void writeMethodMeasure(MethodHandler mh) {
		mh.addVar("clz", Class.class);

		mh.varOp(ILOAD, "data");
		mh.visitMethodInsn(INVOKEVIRTUAL, Object.class, "getClass", Class.class);
		mh.varOp(ISTORE, "clz");

		mh.op(ICONST_1);

		ArrayUtil.dualFor(this.subClasses, this.subDefs, (clz, serializerDef) -> {
			mh.varOp(ILOAD, "clz");
			mh.visitLdcInsn(Type.getType(clz));

			Label next = new Label();

			mh.visitJumpInsn(IF_ACMPNE, next);
			serializerDef.writeMeasure(mh, () -> {
				mh.varOp(ILOAD, "data");
				if(!clz.isAssignableFrom(this.clazz.getBytecodeClass()))
					mh.visitTypeInsn(CHECKCAST, clz);
			});
			mh.op(IADD, IRETURN);

			mh.visitLabel(next);
		});

		// TODO: throw

		mh.op(IRETURN);
	}
}
