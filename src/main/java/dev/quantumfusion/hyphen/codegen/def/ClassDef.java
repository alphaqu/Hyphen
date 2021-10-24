package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.PackedBooleans;
import dev.quantumfusion.hyphen.codegen.Variable;
import dev.quantumfusion.hyphen.codegen.statement.If;
import dev.quantumfusion.hyphen.codegen.statement.IfElse;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.util.GenUtil;
import dev.quantumfusion.hyphen.util.Style;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
			for (FieldEntry field : clazz.getFields()) {
				if (!field.clazz().containsAnnotation(Data.class)) continue;
				try {
					this.fields.put(BytecodeField.create(field), handler.acquireDef(field.clazz()));
				} catch (Throwable throwable) {
					throw HyphenException.thr("field", Style.LINE_RIGHT, field, throwable);
				}

			}

			if (!handler.options.get(Options.DISABLE_PUT)) {
				List<Class<?>> constructorParameters = new ArrayList<>();
				for (FieldEntry field : new Clazz(handler, clazz.getDefinedClass()).getFields()) {
					if (!field.clazz().containsAnnotation(Data.class)) continue;
					constructorParameters.add(field.clazz().getDefinedClass());
				}
				this.constructorParameters = constructorParameters.toArray(Class[]::new);
				try {
					if (!Modifier.isPublic(aClass.getConstructor(this.constructorParameters).getModifiers()))
						throw new HyphenException("Could not access constructor", "Check if the constructor is public.");

				} catch (NoSuchMethodException e) {
					throw new HyphenException(e.getMessage(), "Check if the constructor holds all of the fields.");
				}
			} else {
				this.constructorParameters = null;
			}
		} catch (Throwable throwable) {
			throw HyphenException.thr("class", Style.LINE_DOWN, clazz, throwable);
		}
	}

	@Override
	protected void writeMethodGet(MethodHandler mh) {
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
			} else if (compactBooleans && fieldEntry.fieldType == boolean.class) {
				info.getBoolean(mh);
			} else {
				entry.getValue().writeGet(mh);
			}
		}
		mh.callInst(INVOKESPECIAL, aClass, "<init>", Void.TYPE, constructorParameters);
	}

	@Override
	protected void writeMethodPut(MethodHandler mh, Runnable valueLoad) {
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
	public int staticSize() {
		if (this.fields.isEmpty()) return 0;

		int size = 0;
		int booleans = 0;
		var compactBooleans = this.options.get(Options.COMPACT_BOOLEANS);
		for (var entry : this.fields.entrySet()) {
			var field = entry.getKey();
			if (compactBooleans && field.fieldType == boolean.class) {
				booleans++;
				continue;
			}

			if (field.nullable) {
				booleans++;
			} else {
				size += entry.getValue().staticSize();
			}
		}

		size += (booleans + 7) >> 3;
		return size;
	}

	@Override
	public boolean hasDynamicSize() {
		// TODO: actually check if any field has dynamic sizes
		return true;// !this.fields.isEmpty();
	}

	@Override
	protected void writeMethodMeasure(MethodHandler mh, Runnable valueLoad, boolean includeStatic) {
		if (this.fields.isEmpty())
			if (includeStatic) mh.visitLdcInsn(this.staticSize());
			else mh.op(ICONST_0);
		else {
			var compactBooleans = this.options.get(Options.COMPACT_BOOLEANS);
			int i = 0;
			if (includeStatic) {
				var staticSize = this.staticSize();
				if (staticSize != 0) {
					i = 1;
					mh.visitLdcInsn(staticSize);
				}
			}

			for (var entry : this.fields.entrySet()) {
				if (compactBooleans && entry.getKey().fieldType == boolean.class) continue;

				var field = entry.getKey();
				var fieldDef = entry.getValue();
				var staticSize = fieldDef.staticSize();
				var dynamicSize = fieldDef.hasDynamicSize();
				if (field.nullable) {
					if (dynamicSize) {
						var cache = mh.addVar(field.fieldName + "_cache", field.fieldType);
						field.loadField(mh, aClass, valueLoad);
						mh.op(DUP);
						mh.varOp(ISTORE, cache);
						try (var anIf = new IfElse(mh, IFNULL)) {
							fieldDef.writeMeasure(mh, () -> mh.varOp(ILOAD, cache));

							if (staticSize != 0) {
								mh.visitLdcInsn(staticSize);
								mh.op(IADD);
							}
							measureAdd(mh, i++, anIf);
						}
					} else {
						if (staticSize != 0) {
							field.loadField(mh, aClass, valueLoad);
							try (var anIf = new IfElse(mh, IFNULL)) {
								mh.visitLdcInsn(staticSize);
								measureAdd(mh, i++, anIf);
							}
						}
					}
				} else {
					if (dynamicSize) {
						fieldDef.writeMeasure(mh, () -> field.loadField(mh, aClass, valueLoad));
						if (i++ > 0) mh.op(IADD);
					}
				}
			}
			if (i == 0) {
				// TODO: missed constant size
				mh.op(ICONST_0);
			}
		}
	}

	private void measureAdd(MethodHandler mh, int i, IfElse anIf) {
		// if we arent the first, just add
		if (i > 0) {
			mh.op(IADD);
			anIf.elseStart();
		} else {
			// else we need to push 0 for the null case
			anIf.elseStart();
			mh.op(ICONST_0);
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
