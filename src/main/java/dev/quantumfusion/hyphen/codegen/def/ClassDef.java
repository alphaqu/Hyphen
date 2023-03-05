package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.SerializerGenerator;
import dev.quantumfusion.hyphen.codegen.MethodWriter;
import dev.quantumfusion.hyphen.codegen.PackedBooleans;
import dev.quantumfusion.hyphen.codegen.Variable;
import dev.quantumfusion.hyphen.codegen.statement.If;
import dev.quantumfusion.hyphen.codegen.statement.IfElse;
import dev.quantumfusion.hyphen.scan.StructField;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.struct.ClassStruct;
import dev.quantumfusion.hyphen.scan.struct.Struct;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.util.GenUtil;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public final class ClassDef extends MethodDef<ClassStruct> {
	private final List<ClassField> fields = new ArrayList<>();

	private Class<?>[] constructorParameters;
	private Class<?> aClass;
	private boolean shouldCompactBooleans;

	public ClassDef(ClassStruct struct) {
		super(struct);
	}

	@Override
	public void scan(SerializerGenerator<?, ?> handler) {
		super.scan(handler);
		this.aClass = struct.getValueClass();
		this.shouldCompactBooleans = handler.isEnabled(Options.COMPACT_BOOLEANS);
		boolean record = aClass.isRecord();
		try {
			List<StructField> classFields = struct.getAllFields(handler.scanner);

			for (StructField field : classFields) {
				if (shouldFieldSerialize(field)) {
					try {
						SerializerDef<?> serializerDef;
						serializerDef = handler.acquireDef(field.type);

						ClassFieldAccess access = null;
						String fieldName = field.field.getName();
						if (record) {
							access = ClassFieldAccess.Record;
						} else if (Modifier.isPublic(field.field.getModifiers())) {
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
						throw HyphenException.rethrow(struct, "field " + "\"" + field.field.getName() + "\"", throwable);
					}
				}
			}

			if (!handler.isEnabled(Options.DISABLE_PUT)) {
				Struct scan = handler.scanner.scan(struct.aClass, null);
				List<Class<?>> constructorParameters = new ArrayList<>();
				for (StructField classField : ((ClassStruct) scan).getAllFields(handler.scanner)) {
					if (shouldFieldSerialize(classField)) {
						constructorParameters.add(classField.type.getValueClass());
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
			throw HyphenException.rethrow(struct, null, throwable);
		}
	}

	private boolean shouldFieldSerialize(StructField field) {
		return !Modifier.isTransient(field.field.getModifiers());
	}

	@Override
	protected void writeMethodGet(MethodWriter mh) {
		var packedBooleans = new PackedBooleans();
		for (var entry : fields) {
			if (entry.isNullable() || shouldCompactBoolean(entry.fieldEntry)) {
				packedBooleans.countBoolean();
			}
		}
		packedBooleans.writeGet(mh);
		mh.typeOp(NEW, aClass);
		mh.op(DUP);
		for (int i = 0; i < fields.size(); i++) {
			var entry = fields.get(i);
			var fieldEntry = entry.fieldEntry;
			if (entry.isNullable()) {
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
			GenUtil.ensureCasted(mh, constructorParameters[i], fieldEntry.type.getBytecodeClass());
		}
		mh.callInst(INVOKESPECIAL, aClass, "<init>", Void.TYPE, constructorParameters);
	}

	@Override
	protected void writeMethodPut(MethodWriter mh, Runnable valueLoad) {
		var info = new PackedBooleans();
		for (var entry : fields) {
			var fieldEntry = entry.fieldEntry;
			if (entry.isNullable()) {
				info.initBoolean(mh);
				loadField(mh, entry, valueLoad);
				mh.op(DUP);
				mh.varOp(ISTORE, mh.addVar(fieldEntry.field.getName() + "temp", fieldEntry.type.getValueClass()));
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
			if (entry.isNullable()) {
				final Variable cache = mh.getVar(fieldEntry.field.getName() + "temp");
				mh.varOp(ILOAD, cache);
				try (var i = new If(mh, IFNULL)) {
					entry.def.writePut(mh, () -> mh.varOp(ILOAD, cache));
				}
			} else {
				entry.def.writePut(mh, () -> loadField(mh, entry, valueLoad));
			}
		}
	}

	private boolean shouldCompactBoolean(StructField fieldEntry) {
		return (shouldCompactBooleans && fieldEntry.type.getValueClass() == boolean.class);
	}

	@Override
	public long getStaticSize() {
		if (this.fields.isEmpty()) {
			return 0;
		}

		int size = 0;
		int booleans = 0;
		for (var entry : this.fields) {
			if (entry.isNullable() || shouldCompactBoolean(entry.fieldEntry)) {
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
	protected void writeMethodMeasure(MethodWriter mh, Runnable valueLoad) {
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
				if (entry.isNullable()) {
					if (isDynamicSize) {
						loadField(mh, entry, valueLoad);
						var cache = mh.addVar(fieldEntry.field.getName() + "temp", fieldEntry.type.getValueClass());
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

	private void ifNullMeasureWrite(MethodWriter mh, int i, IfElse anIf) {
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

	private void loadField(MethodWriter mh, ClassField entry, Runnable dataLoad) {
		dataLoad.run();
		var clazz = entry.fieldEntry.type;
		var field = entry.fieldEntry.field;
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
		GenUtil.ensureCasted(mh, clazz.getValueClass(), bytecodeClass);
	}


	public record ClassField(SerializerDef<?> def, StructField fieldEntry, ClassFieldAccess access) {
		public boolean isNullable() {
			return fieldEntry.type.isAnnotationPresent(DataNullable.class);
		}
	}

	public enum ClassFieldAccess {
		Record,
		Field,
		Getter,
	}
}
