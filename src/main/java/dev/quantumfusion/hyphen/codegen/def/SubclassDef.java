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

public class SubclassDef extends MethodDef {
	private final Class<?>[] subClasses;
	private final SerializerDef[] subDefs;

	public SubclassDef(SerializerHandler<?, ?> handler, Clazz clazz, Class<?>[] subClasses) {
		super(handler.codegenHandler, clazz, "SUB{" + clazz + " # " + Arrays.stream(subClasses).map(Class::getSimpleName).collect(Collectors.joining(", ")) + "}");
		this.subClasses = subClasses;
		this.subDefs = ArrayUtil.map(subClasses, SerializerDef[]::new, subclass -> handler.acquireDef(clazz.asSub(subclass)));
	}

	@Override
	public void writeMethodGet(MethodHandler mh) {
		mh.varOp(ILOAD, "io");
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
	public void writeMethodPut(MethodHandler mh, String dataVar) {
		mh.addVar("clz", Class.class);
		mh.varOp(ILOAD, dataVar);
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
					mh.varOp(ILOAD, dataVar);
					GenUtil.shouldCastGeneric(mh ,clz, this.clazz.getBytecodeClass());
				});
				mh.op(RETURN);
			}
		});
		// TODO: throw
	}

	@Override
	public void writeMethodMeasure(MethodHandler mh, String dataVar) {
		mh.addVar("clz", Class.class);
		mh.varOp(ILOAD, dataVar);
		mh.callInst(INVOKEVIRTUAL, Object.class, "getClass", Class.class);
		mh.varOp(ISTORE, "clz");

		mh.op(ICONST_1);
		ArrayUtil.dualFor(this.subClasses, this.subDefs, (clz, serializerDef) -> {
			mh.varOp(ILOAD, "clz");
			mh.visitLdcInsn(Type.getType(clz));
			try (var anIf = new If(mh, IF_ACMPNE)) {
				serializerDef.writeMeasure(mh, () -> {
					mh.varOp(ILOAD, dataVar);
					GenUtil.shouldCastGeneric(mh ,clz, this.clazz.getBytecodeClass());
				});
				mh.op(IADD, IRETURN);
			}
		});
		// TODO: throw
	}
}
