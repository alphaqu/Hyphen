package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class ClassDef extends MethodDef {
	protected final Map<FieldEntry, SerializerDef> fields = new LinkedHashMap<>();
	protected final Class<?> aClass;

	public ClassDef(SerializerHandler<?, ?> handler, Clazz clazz) {
		super(handler.codegenHandler, clazz);
		this.aClass = clazz.getDefinedClass();
		for (FieldEntry field : clazz.getFields()) {
			fields.put(field, handler.acquireDef(field.clazz()));
		}
	}

	@Override
	public void writeMethodGet(MethodHandler mh) {
		mh.visitTypeInsn(NEW, aClass);
		mh.op(DUP);
		List<Class<?>> constParameters = new ArrayList<>();
		fields.forEach((fieldEntry, def) -> {
			mh.varOp(ILOAD,  "io");
			def.writeGet(mh);
			// TODO fix constructors
			constParameters.add(fieldEntry.clazz().getBytecodeClass());
		});
		//TODO add generic support
		mh.visitMethodInsn(INVOKESPECIAL, aClass, "<init>", Void.TYPE, constParameters.toArray(Class[]::new));
		mh.op(ARETURN);
	}

	@Override
	public void writeMethodPut(MethodHandler mh) {
		fields.forEach((fieldEntry, def) -> {
			mh.varOp(ILOAD,  "io", "data");
			//TODO add get method support / generic support
			mh.visitFieldInsn(GETFIELD, aClass, fieldEntry.field().getName(), fieldEntry.field().getType());
			final Clazz clazz = fieldEntry.clazz();
			if (clazz.getDefinedClass() != clazz.getBytecodeClass()) {
				mh.visitTypeInsn(CHECKCAST, clazz.getDefinedClass());
			}
			def.writePut(mh);
		});
		mh.op(RETURN);
	}

	@Override
	public void writeMethodMeasure(MethodHandler mh) {
		int i = 0;
		for (var entry : fields.entrySet()) {
			var field = entry.getKey().field();
			entry.getValue().writeMeasure(mh, () -> {
				mh.varOp(ILOAD,  "data");
				mh.visitFieldInsn(GETFIELD, aClass, field.getName(), field.getType());
				final Clazz clazz = entry.getKey().clazz();
				if (clazz.getDefinedClass() != clazz.getBytecodeClass()) {
					mh.visitTypeInsn(CHECKCAST, clazz.getDefinedClass());
				}
			});
			if (i++ != 0) {
				mh.op(IADD);
			}
		}
		mh.op(IRETURN);
	}
}
