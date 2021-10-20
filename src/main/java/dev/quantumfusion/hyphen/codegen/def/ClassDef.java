package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.PackedBooleans;
import dev.quantumfusion.hyphen.codegen.Variable;
import dev.quantumfusion.hyphen.codegen.statement.If;
import dev.quantumfusion.hyphen.codegen.statement.IfElse;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.util.GenUtil;
import dev.quantumfusion.hyphen.util.Style;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class ClassDef extends MethodDef {
	protected final Map<BytecodeField, SerializerDef> fields = new LinkedHashMap<>();
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
					this.fields.put(BytecodeField.create(field), handler.acquireDef(field.clazz()));
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
		var info = new PackedBooleans();
		var compactBooleans = options.get(Options.COMPACT_BOOLEANS);
		for (var entry : fields.entrySet()) if (entry.getKey().nullable) info.countBoolean();
		info.writeGet(mh);
		mh.typeOp(NEW, aClass);
		mh.op(DUP);
		for (var entry : fields.entrySet()) {
			var fieldEntry = entry.getKey();
			if (fieldEntry.nullable) {
				info.getBoolean(mh);
				mh.op(ICONST_0);
				try (var anIf = new IfElse(mh, IF_ICMPNE)) {
					entry.getValue().writeGet(mh);
					anIf.elseStart();
					mh.op(ACONST_NULL);
				}
			} else if (compactBooleans && fieldEntry.fieldType == boolean.class)  {
				info.getBoolean(mh);
			} else {
				entry.getValue().writeGet(mh);
			}
		}
		mh.callInst(INVOKESPECIAL, aClass, "<init>", Void.TYPE, constructorParameters);
	}

	@Override
	public void writeMethodPut(MethodHandler mh, Runnable valueLoad) {
		var info = new PackedBooleans();
		var compactBooleans = options.get(Options.COMPACT_BOOLEANS);
		for (var entry : fields.entrySet()) {
			var fieldEntry = entry.getKey();
			if (fieldEntry.nullable) {
				info.initBoolean(mh);
				fieldEntry.loadField(mh, aClass, valueLoad);
				mh.op(DUP);
				mh.varOp(ISTORE, mh.addVar(fieldEntry.fieldName + "_cache", fieldEntry.fieldType));
				try (var anIf = new IfElse(mh, IFNULL)) {
					info.falseBoolean(mh);
					anIf.elseStart();
					info.trueBoolean(mh);
				}
			} else if (compactBooleans && fieldEntry.fieldType == boolean.class) {
				info.initBoolean(mh);
				fieldEntry.loadField(mh, aClass, valueLoad);
				info.consumeBoolean(mh);
			}
		}
		info.writePut(mh);

		for (var entry : fields.entrySet()) {
			var fieldEntry = entry.getKey();
			if (compactBooleans && fieldEntry.fieldType == boolean.class) continue;
			if (fieldEntry.nullable) {
				final Variable cache = mh.getVar(fieldEntry.fieldName + "_cache");
				mh.varOp(ILOAD, cache);
				try (var i = new If(mh, IFNULL)) {
					entry.getValue().writePut(mh, () -> mh.varOp(ILOAD, cache));
				}
			} else {
				entry.getValue().writePut(mh, () -> fieldEntry.loadField(mh, aClass, valueLoad));
			}
		}
	}

	@Override
	public void writeMethodMeasure(MethodHandler mh, Runnable valueLoad) {
		if (fields.size() == 0) mh.op(ICONST_0);
		else {
			var info = new PackedBooleans();
			var compactBooleans = options.get(Options.COMPACT_BOOLEANS);
			int i = 0;
			for (var entry : fields.entrySet()) {
				var field = entry.getKey();
				if (compactBooleans && field.fieldType == boolean.class) {
					info.countBoolean();
					continue;
				}

				if (field.nullable) {
					var cache = mh.addVar(field.fieldName + "_cache", field.fieldType);
					info.countBoolean();
					field.loadField(mh, aClass, valueLoad);
					mh.op(DUP);
					mh.varOp(ISTORE, cache);
					try (var anIf = new IfElse(mh, IFNULL)) {
						entry.getValue().writeMeasure(mh, () -> mh.varOp(ILOAD, cache));
						anIf.elseStart();
						mh.op(ICONST_0);
					}
				} else entry.getValue().writeMeasure(mh, () -> field.loadField(mh, aClass, valueLoad));

				if (i++ > 0) mh.op(IADD);
			}

			if (info.getBytes() > 0) {
				mh.visitLdcInsn(info.getBytes());
				if (i > 0) mh.op(IADD);
			}
		}
	}

	private record BytecodeField(Clazz clazz, Class<?> fieldType, String fieldName, int access, boolean nullable) {
		public static BytecodeField create(FieldEntry source) {
			final Field field = source.field();
			return new BytecodeField(source.clazz(), field.getType(), field.getName(), field.getModifiers(), source.clazz().containsAnnotation(DataNullable.class));
		}

		private void loadField(MethodHandler mh, Class<?> holder, Runnable dataLoad) {
			dataLoad.run();
			var clazz = this.clazz();
			var definedClass = clazz.getDefinedClass();
			var bytecodeClass = clazz.getBytecodeClass();

			var fieldName = this.fieldName;
			if (Modifier.isPublic(this.access)) mh.visitFieldInsn(GETFIELD, holder, fieldName, this.fieldType);
			else if (holder.isRecord()) mh.callInst(INVOKEVIRTUAL, holder, fieldName, bytecodeClass);
			else try {
					definedClass.getDeclaredMethod("get" + GenUtil.upperCase(fieldName));
					mh.callInst(INVOKEVIRTUAL, holder, fieldName, bytecodeClass);
				} catch (NoSuchMethodException ignored) {
					throw new HyphenException("Could not find a way to access \"" + fieldName + "\"",
											  "Try making the field public or add a getter");
				}

			GenUtil.shouldCastGeneric(mh, definedClass, bytecodeClass);
		}
	}
}
