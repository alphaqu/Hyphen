package dev.notalpha.hyphen.codegen.def;

import dev.notalpha.hyphen.SerializerGenerator;
import dev.notalpha.hyphen.codegen.MethodWriter;
import dev.notalpha.hyphen.codegen.statement.IfElse;
import dev.notalpha.hyphen.scan.annotations.DataNullable;
import dev.notalpha.hyphen.scan.struct.ClassStruct;
import dev.notalpha.hyphen.thr.HyphenException;
import dev.notalpha.hyphen.util.GenUtil;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import java.lang.invoke.ConstantBootstraps;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import static org.objectweb.asm.Opcodes.*;

public final class EnumDef extends MethodDef<ClassStruct> {
	public static boolean USE_CONSTANT_DYNAMIC = false;
	public static boolean USE_CONSTANT_DYNAMIC_INVOKE = false;

	private Class<? extends Enum<?>> en;
	private int enSize;
	private Class<?> enumSizePrimitive;
	private boolean isNullable;

	public EnumDef(ClassStruct clazz) {
		super(clazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void scan(SerializerGenerator<?, ?> handler) {
		super.scan(handler);
		this.en = (Class<? extends Enum<?>>) struct.getValueClass();

		this.isNullable = struct.isAnnotationPresent(DataNullable.class);
		this.enSize = this.en.getEnumConstants().length + (this.isNullable ? 1 : 0);

		if (this.enSize == 0) {
			throw new HyphenException("Enum does not contain any values", "Make the enum nullable");
		} else if (this.enSize <= 0xff) {
			this.enumSizePrimitive = byte.class;
		} else if (this.enSize <= 0xffff) {
			this.enumSizePrimitive = short.class;
		} else {
			this.enumSizePrimitive = int.class;
		}
	}

	@SuppressWarnings({"unchecked", "unused"})
	public static <T extends Enum<T>> T[] getValues(MethodHandles.Lookup lookup, String name, Class<T[]> cls) {
		return (T[]) cls.componentType().getEnumConstants();
	}

	@Override
	protected void writeMethodGet(MethodWriter mh) {
		Label end = null;
		if (this.isNullable) {
			var l = new Label();
			end = new Label();
			mh.loadIO();
			mh.getIO(this.enumSizePrimitive);

			mh.op(DUP);
			// index | index
			mh.visitJumpInsn(IFGE, l);
			mh.op(POP, ACONST_NULL);
			// null
			mh.visitJumpInsn(GOTO, end);
			mh.visitLabel(l);
			// index
		}

		if (USE_CONSTANT_DYNAMIC) {
			if (USE_CONSTANT_DYNAMIC_INVOKE) {
				mh.visitLdcInsn(new ConstantDynamic(
						this.en.getSimpleName() + "$Values",
						Type.getDescriptor(this.en.arrayType()),
						GenUtil.createHandle(H_INVOKESTATIC, ConstantBootstraps.class, "invoke", false, Object.class,
								MethodHandles.Lookup.class, String.class, Class.class, MethodHandle.class, Object[].class),
						GenUtil.createHandle(H_INVOKESTATIC, this.en, "values", false, this.en.arrayType())
				));
			} else {
				mh.visitLdcInsn(new ConstantDynamic(
						this.en.getSimpleName() + "$Values",
						Type.getDescriptor(this.en.arrayType()),
						GenUtil.createHandle(H_INVOKESTATIC, EnumDef.class, "getValues", false, Enum[].class,
								MethodHandles.Lookup.class, String.class, Class.class)
				));
			}
		} else {
			mh.callInst(INVOKESTATIC, this.en, "values", this.en.arrayType());
			//mh.visitFieldInsn(GETSTATIC, this.en, "VAL", this.en.arrayType());
		}

		if (this.isNullable) {
			// index | enumValues
			mh.op(SWAP);
			// enumValues | index
		} else {
			// enumValues
			mh.loadIO();
			mh.getIO(this.enumSizePrimitive);
			// enumValues | index
		}

		// enumValues | index

		mh.op(AALOAD);
		if (end != null) {
			mh.visitLabel(end);
		}
	}

	@Override
	protected void writeMethodPut(MethodWriter mh, Runnable valueLoad) {
		mh.loadIO();
		valueLoad.run();

		if (this.isNullable) {
			try (IfElse ifElse = new IfElse(mh, IFNONNULL)) {
				// if null
				mh.op(ICONST_M1);

				ifElse.elseEnd();
				// if not null
				valueLoad.run();
				mh.callInst(INVOKEVIRTUAL, Enum.class, "ordinal", int.class);
			}
			mh.putIO(this.enumSizePrimitive);
		} else {
			mh.callInst(INVOKEVIRTUAL, Enum.class, "ordinal", int.class);
			mh.putIO(this.enumSizePrimitive);
		}
	}

	@Override
	public long getStaticSize() {
		return this.enumSizePrimitive == null ? 0 : PrimitiveIODef.getSize(this.enumSizePrimitive);
	}

	@Override
	public boolean hasDynamicSize() {
		return false;
	}

	@Override
	protected void writeMethodMeasure(MethodWriter mh, Runnable valueLoad) {
		throw new UnsupportedOperationException("Enums don't have a dynamic size");
	}
}
