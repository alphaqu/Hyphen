package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.util.GenUtil;
import dev.quantumfusion.hyphen.util.Style;

import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class ClassDef extends MethodDef {
	protected final Map<FieldEntry, SerializerDef> fields = new LinkedHashMap<>();
	protected final Class<?>[] constructorParameters;
	protected final Class<?> aClass;

	public ClassDef(SerializerHandler<?, ?> handler, Clazz clazz) {
		super(handler.codegenHandler, clazz);
		this.aClass = clazz.getDefinedClass();
		try {
			var fields = clazz.getFields();
			constructorParameters = new Class[fields.size()];
			for (int i = 0; i < fields.size(); i++) {
				FieldEntry field = fields.get(i);

				constructorParameters[i] = field.clazz().getDefinedClass();
				try {
					this.fields.put(field, handler.acquireDef(field.clazz()));
				} catch (Throwable throwable) {
					throw HyphenException.thr("field", Style.LINE_RIGHT, field, throwable);
				}
			}
		} catch (Throwable throwable) {
			throw HyphenException.thr("class", Style.LINE_DOWN, clazz, throwable);
		}
	}

	@Override
	public void writeMethodGet(MethodHandler mh) {
		mh.typeOp(NEW, aClass);
		mh.op(DUP);
		fields.values().forEach(def -> def.writeGet(mh));
		mh.callInst(INVOKESPECIAL, aClass, "<init>", Void.TYPE, constructorParameters);
	}

	@Override
	public void writeMethodPut(MethodHandler mh, Runnable valueLoad) {
		fields.forEach((fieldEntry, def) -> def.writePut(mh, () -> loadField(mh, fieldEntry, valueLoad)));
	}

	@Override
	public void writeMethodMeasure(MethodHandler mh, Runnable valueLoad) {
		if (fields.size() == 0) mh.op(ICONST_0);
		else {
			int i = 0;
			for (var entry : fields.entrySet()) {
				entry.getValue().writeMeasure(mh, () -> loadField(mh, entry.getKey(), valueLoad));
				if (i++ > 0) mh.op(IADD);
			}
		}
	}

	private void loadField(MethodHandler mh, FieldEntry entry, Runnable dataLoad) {
		dataLoad.run();
		var clazz = entry.clazz();
		var field = entry.field();
		var definedClass = clazz.getDefinedClass();
		var bytecodeClass = clazz.getBytecodeClass();

		var fieldName = field.getName();
		if (Modifier.isPublic(field.getModifiers())) mh.visitFieldInsn(GETFIELD, aClass, fieldName, field.getType());
		else if (definedClass.isRecord()) mh.callInst(INVOKEVIRTUAL, aClass, fieldName, bytecodeClass);
		else try {
				definedClass.getDeclaredMethod("get" + GenUtil.upperCase(fieldName));
				mh.callInst(INVOKEVIRTUAL, aClass, fieldName, bytecodeClass);
			} catch (NoSuchMethodException ignored) {
				throw new HyphenException("Could not find a way to access \"" + fieldName + "\"",
										  "Try making the field public or add a getter");
			}

		GenUtil.shouldCastGeneric(mh, definedClass, bytecodeClass);
	}
}
