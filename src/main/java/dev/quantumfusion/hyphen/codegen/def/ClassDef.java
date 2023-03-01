package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.PackedBooleans;
import dev.quantumfusion.hyphen.codegen.Variable;
import dev.quantumfusion.hyphen.codegen.statement.If;
import dev.quantumfusion.hyphen.codegen.statement.IfElse;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.util.GenUtil;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public final class ClassDef extends MethodDef {
	private final List<ClassField> fields = new ArrayList<>();

	private Class<?>[] constructorParameters;
	private Class<?> aClass;

	public ClassDef(SerializerHandler<?, ?> handler, Clazz clazz) {
		super(handler, clazz);
	}

	@Override
	public void scan(SerializerHandler<?, ?> handler, Clazz clazz) {
		this.aClass = clazz.getDefinedClass();
		boolean record = aClass.isRecord();
		try {
			for (FieldEntry field : clazz.getFields(handler)) {
				if (shouldFieldSerialize(field)) {
					try {
						SerializerDef serializerDef = handler.acquireDef(field.clazz());
						ClassFieldAccess access = null;
						String fieldName = field.field().getName();
						if (record) {
							access = ClassFieldAccess.Record;
						} else if (Modifier.isPublic(field.field().getModifiers())) {
							access = ClassFieldAccess.Field;
						} else {
							try {
								String name = "get" + GenUtil.upperCase(fieldName);
								aClass.getDeclaredMethod(name);
								access = ClassFieldAccess.Getter;
							} catch (NoSuchMethodException ignored) {
							}
						}

						if (access == null) {
							throw new HyphenException("Could not find a way to access \"" + fieldName + "\"", "Try making the field public or add a getter");
						}

						this.fields.add(new ClassField(serializerDef, field, access));
					} catch (Throwable throwable) {
						throw HyphenException.rethrow(clazz, "field " + field.getFieldName(), throwable);
					}
				}
			}

			if (!handler.options.get(Options.DISABLE_PUT)) {
				List<Class<?>> constructorParameters = new ArrayList<>();
				for (FieldEntry field : Clazz.create(clazz.getDefinedClass()).getFields(handler)) {
					if (shouldFieldSerialize(field)) {
						constructorParameters.add(field.clazz().getDefinedClass());
					}
				}
				this.constructorParameters = constructorParameters.toArray(Class[]::new);
				try {
					if (!Modifier.isPublic(aClass.getConstructor(this.constructorParameters).getModifiers())) {
						throw new HyphenException("Could not access constructor", "Check if the constructor is public.");
					}

				} catch (NoSuchMethodException e) {
					throw new HyphenException(e.getMessage(), "Check if the constructor holds all of the fields.");
				}
			} else {
				this.constructorParameters = null;
			}
		} catch (Throwable throwable) {
			throw HyphenException.rethrow(clazz, null, throwable);
		}
	}

	private boolean shouldFieldSerialize(FieldEntry field) {
		return !Modifier.isTransient(field.field().getModifiers());
	}

	@Override
	protected void writeMethodGet(MethodHandler mh) {
		var packedBooleans = new PackedBooleans();
		for (var entry : fields) {
			if (entry.fieldEntry.isNullable() || shouldCompactBoolean(entry.fieldEntry)) {
				packedBooleans.countBoolean();
			}
		}
		packedBooleans.writeGet(mh);
		mh.typeOp(NEW, aClass);
		mh.op(DUP);
		for (var entry : fields) {
			var fieldEntry = entry.fieldEntry;
			if (fieldEntry.isNullable()) {
				packedBooleans.getBoolean(mh);
				try (var anIf = new IfElse(mh, IFNE)) {
					entry.def.writeGet(mh);
					anIf.elseEnd();
					mh.op(ACONST_NULL);
				}
			} else if (shouldCompactBoolean(fieldEntry)) {
				packedBooleans.getBoolean(mh);
			} else {
				entry.def.writeGet(mh);
			}
		}
		mh.callInst(INVOKESPECIAL, aClass, "<init>", Void.TYPE, constructorParameters);
	}

	@Override
	protected void writeMethodPut(MethodHandler mh, Runnable valueLoad) {
		var info = new PackedBooleans();
		for (var entry : fields) {
			var fieldEntry = entry.fieldEntry;
			if (fieldEntry.isNullable()) {
				info.initBoolean(mh);
				loadField(mh, entry, valueLoad);
				mh.op(DUP);
				mh.varOp(ISTORE, mh.addVar(fieldEntry.getFieldName() + "temp", fieldEntry.getFieldType()));
				try (var anIf = new IfElse(mh, IFNULL)) {
					info.falseBoolean(mh);
					anIf.elseEnd();
					info.trueBoolean(mh);
				}
			} else if (shouldCompactBoolean(fieldEntry)) {
				info.initBoolean(mh);
				loadField(mh, entry, valueLoad);
				info.consumeBoolean(mh);
			}
		}
		info.writePut(mh);

		for (var entry : fields) {
			var fieldEntry = entry.fieldEntry;
			if (shouldCompactBoolean(fieldEntry)) {
				continue;
			}
			if (fieldEntry.isNullable()) {
				final Variable cache = mh.getVar(fieldEntry.getFieldName() + "temp");
				mh.varOp(ILOAD, cache);
				try (var i = new If(mh, IFNULL)) {
					entry.def.writePut(mh, () -> mh.varOp(ILOAD, cache));
				}
			} else {
				entry.def.writePut(mh, () -> loadField(mh, entry, valueLoad));
			}
		}
	}

	private boolean shouldCompactBoolean(FieldEntry fieldEntry) {
		return (options.get(Options.COMPACT_BOOLEANS) && fieldEntry.getFieldType() == boolean.class);
	}

	@Override
	public long getStaticSize() {
		if (this.fields.isEmpty()) {
			return 0;
		}

		int size = 0;
		int booleans = 0;
		for (var entry : this.fields) {
			if (entry.fieldEntry.isNullable() || shouldCompactBoolean(entry.fieldEntry)) {
				booleans++;
			} else {
				size += entry.def.getStaticSize();
			}
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
		if (this.fields.isEmpty()) {
			mh.op(LCONST_0);
		} else {
			int i = 0;
			for (var entry : this.fields) {
				if (shouldCompactBoolean(entry.fieldEntry)) {
					continue;
				}

				var fieldEntry = entry.fieldEntry;
				var fieldDef = entry.def;
				var staticSize = fieldDef.getStaticSize();
				var isDynamicSize = fieldDef.hasDynamicSize();
				if (fieldEntry.isNullable()) {
					if (isDynamicSize) {
						loadField(mh, entry, valueLoad);
						var cache = mh.addVar(fieldEntry.getFieldName() + "temp", fieldEntry.getFieldType());
						mh.op(DUP);
						mh.varOp(ISTORE, cache);
						try (var anIf = new IfElse(mh, IFNULL)) {
							fieldDef.writeMeasure(mh, () -> mh.varOp(ILOAD, cache));

							if (staticSize != 0) {
								mh.visitLdcInsn(staticSize);
								mh.op(LADD);
							}
							ifNullMeasureWrite(mh, i++, anIf);
						}
					} else {
						if (staticSize != 0) {
							loadField(mh, entry, valueLoad);
							try (var anIf = new IfElse(mh, IFNULL)) {
								mh.visitLdcInsn(staticSize);
								ifNullMeasureWrite(mh, i++, anIf);
							}
						}
					}
				} else {
					if (isDynamicSize) {
						fieldDef.writeMeasure(mh, () -> loadField(mh, entry, valueLoad));
						if (i++ > 0) {
							mh.op(LADD);
						}
					}
				}
			}
			if (i == 0) {
				// TODO: missed constant size
				mh.op(LCONST_0);
			}
		}
	}

	private void ifNullMeasureWrite(MethodHandler mh, int i, IfElse anIf) {
		// if we arent the first, just add
		if (i > 0) {
			mh.op(LADD);
			anIf.elseEnd();
		} else {
			// else we need to push 0 for the null case
			anIf.elseEnd();
			mh.op(LCONST_0);
		}
	}

	private void loadField(MethodHandler mh, ClassField entry, Runnable dataLoad) {
		dataLoad.run();
		var clazz = entry.fieldEntry.clazz();
		var field = entry.fieldEntry.field();
		var bytecodeClass = clazz.getBytecodeClass();
		var fieldName = field.getName();
		switch (entry.access) {
			case Record -> {
				mh.callInst(INVOKEVIRTUAL, aClass, fieldName, bytecodeClass);
			}
			case Field -> {
				mh.visitFieldInsn(GETFIELD, aClass, fieldName, field.getType());
			}
			case Getter -> {
				String name = "get" + GenUtil.upperCase(fieldName);
				mh.callInst(INVOKEVIRTUAL, aClass, name, bytecodeClass);
			}
		}
		GenUtil.ensureCasted(mh, clazz.getDefinedClass(), bytecodeClass);
	}


	public record ClassField(SerializerDef def, FieldEntry fieldEntry, ClassFieldAccess access) {
	}

	public enum ClassFieldAccess {
		Record,
		Field,
		Getter,
	}
}
