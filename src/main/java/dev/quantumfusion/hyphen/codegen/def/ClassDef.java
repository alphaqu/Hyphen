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
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.util.GenUtil;
import dev.quantumfusion.hyphen.util.Style;

import java.lang.reflect.Modifier;
import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public final class ClassDef extends MethodDef {
	private final Map<FieldEntry, SerializerDef> fields = new LinkedHashMap<>();
	private final Set<String> forcedFields;

	private Class<?>[] constructorParameters;
	private Class<?> aClass;
	private boolean record;

	public ClassDef(SerializerHandler<?, ?> handler, Clazz clazz) {
		super(handler, clazz);
		this.forcedFields = Set.of();
	}

	public ClassDef(SerializerHandler<?, ?> handler, Clazz clazz, String... forcedFields) {
		super(handler, clazz);
		this.forcedFields = Set.of(forcedFields);
	}

	@Override
	public void scan(SerializerHandler<?, ?> handler, Clazz clazz) {
		this.aClass = clazz.getDefinedClass();
		this.record = aClass.isRecord();
		try {
			for (FieldEntry field : clazz.getFields()) {
				if (shouldFieldSerialize(field)) {
					try {
						this.fields.put(field, handler.acquireDef(field.clazz()));
					} catch (Throwable throwable) {
						throw HyphenException.thr("field", Style.LINE_RIGHT, field, throwable);
					}
				}
			}

			if (!handler.options.get(Options.DISABLE_PUT)) {
				List<Class<?>> constructorParameters = new ArrayList<>();
				for (FieldEntry field : new Clazz(handler, clazz.getDefinedClass()).getFields()) {
					if (shouldFieldSerialize(field)) {
						constructorParameters.add(field.clazz().getDefinedClass());
					}
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

	private boolean shouldFieldSerialize(FieldEntry field) {
		return forcedFields.contains(field.getFieldName()) || (field.clazz().containsAnnotation(Data.class) && !Modifier.isTransient(field.field().getModifiers()));
	}

	@Override
	protected void writeMethodGet(MethodHandler mh) {
		var packedBooleans = new PackedBooleans();
		for (var entry : fields.entrySet()) if (entry.getKey().isNullable() || shouldCompactBoolean(entry.getKey())) packedBooleans.countBoolean();
		packedBooleans.writeGet(mh);
		mh.typeOp(NEW, aClass);
		mh.op(DUP);
		for (var entry : fields.entrySet()) {
			var fieldEntry = entry.getKey();
			if (fieldEntry.isNullable()) {
				packedBooleans.getBoolean(mh);
				mh.op(ICONST_0);
				try (var anIf = new IfElse(mh, IF_ICMPNE)) {
					entry.getValue().writeGet(mh);
					anIf.elseStart();
					mh.op(ACONST_NULL);
				}
			} else if (shouldCompactBoolean(fieldEntry)) packedBooleans.getBoolean(mh);
			else entry.getValue().writeGet(mh);
		}
		mh.callInst(INVOKESPECIAL, aClass, "<init>", Void.TYPE, constructorParameters);
	}

	@Override
	protected void writeMethodPut(MethodHandler mh, Runnable valueLoad) {
		var info = new PackedBooleans();
		for (var entry : fields.entrySet()) {
			var fieldEntry = entry.getKey();
			if (fieldEntry.isNullable()) {
				info.initBoolean(mh);
				loadField(mh, fieldEntry, valueLoad);
				mh.op(DUP);
				mh.varOp(ISTORE, mh.addVar(fieldEntry.getFieldName() + "_cache", fieldEntry.getFieldType()));
				try (var anIf = new IfElse(mh, IFNULL)) {
					info.falseBoolean(mh);
					anIf.elseStart();
					info.trueBoolean(mh);
				}
			} else if (shouldCompactBoolean(fieldEntry)) {
				info.initBoolean(mh);
				loadField(mh, fieldEntry, valueLoad);
				info.consumeBoolean(mh);
			}
		}
		info.writePut(mh);

		for (var entry : fields.entrySet()) {
			var fieldEntry = entry.getKey();
			if (shouldCompactBoolean(fieldEntry)) continue;
			if (fieldEntry.isNullable()) {
				final Variable cache = mh.getVar(fieldEntry.getFieldName() + "_cache");
				mh.varOp(ILOAD, cache);
				try (var i = new If(mh, IFNULL)) {
					entry.getValue().writePut(mh, () -> mh.varOp(ILOAD, cache));
				}
			} else {
				entry.getValue().writePut(mh, () -> loadField(mh, fieldEntry, valueLoad));
			}
		}
	}

	private boolean shouldCompactBoolean(FieldEntry fieldEntry) {
		return (options.get(Options.COMPACT_BOOLEANS) && fieldEntry.getFieldType() == boolean.class);
	}

	@Override
	public int getStaticSize() {
		if (this.fields.isEmpty()) return 0;

		int size = 0;
		int booleans = 0;
		for (var entry : this.fields.entrySet()) {
			if (entry.getKey().isNullable() || shouldCompactBoolean(entry.getKey())) booleans++;
			else size += entry.getValue().getStaticSize();
		}

		return size + ((booleans + 7) >> 3);
	}

	@Override
	public boolean hasDynamicSize() {
		// TODO: actually check if any field has dynamic sizes
		return true;// !this.fields.isEmpty();
	}

	@Override
	protected void writeMethodMeasure(MethodHandler mh, Runnable valueLoad) {
		if (this.fields.isEmpty()) mh.op(ICONST_0);
		else {
			int i = 0;
			for (var entry : this.fields.entrySet()) {
				if (shouldCompactBoolean(entry.getKey())) continue;

				var fieldEntry = entry.getKey();
				var fieldDef = entry.getValue();
				var staticSize = fieldDef.getStaticSize();
				var isDynamicSize = fieldDef.hasDynamicSize();
				if (fieldEntry.isNullable()) {
					if (isDynamicSize) {
						loadField(mh, fieldEntry, valueLoad);
						var cache = mh.addVar(fieldEntry.getFieldName() + "_cache", fieldEntry.getFieldType());
						mh.op(DUP);
						mh.varOp(ISTORE, cache);
						try (var anIf = new IfElse(mh, IFNULL)) {
							fieldDef.writeMeasure(mh, () -> mh.varOp(ILOAD, cache));

							if (staticSize != 0) {
								mh.visitLdcInsn(staticSize);
								mh.op(IADD);
							}
							ifNullMeasureWrite(mh, i++, anIf);
						}
					} else {
						if (staticSize != 0) {
							loadField(mh, fieldEntry, valueLoad);
							try (var anIf = new IfElse(mh, IFNULL)) {
								mh.visitLdcInsn(staticSize);
								ifNullMeasureWrite(mh, i++, anIf);
							}
						}
					}
				} else {
					if (isDynamicSize) {
						fieldDef.writeMeasure(mh, () -> loadField(mh, fieldEntry, valueLoad));
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

	private void ifNullMeasureWrite(MethodHandler mh, int i, IfElse anIf) {
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

	private void loadField(MethodHandler mh, FieldEntry fieldEntry, Runnable dataLoad) {
		dataLoad.run();
		var clazz = fieldEntry.clazz();
		var field = fieldEntry.field();
		var definedClass = clazz.getDefinedClass();
		var bytecodeClass = clazz.getBytecodeClass();

		var fieldName = field.getName();
		if (record) mh.callInst(INVOKEVIRTUAL, aClass, fieldName, bytecodeClass);
		else if (Modifier.isPublic(field.getModifiers()))
			mh.visitFieldInsn(GETFIELD, aClass, fieldName, field.getType());
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
