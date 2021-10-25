package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Type;

import java.lang.invoke.ConstantBootstraps;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import static org.objectweb.asm.Opcodes.*;

public final class EnumDef extends MethodDef {
	public static boolean USE_CONSTANT_DYNAMIC = true;
	public static boolean USE_CONSTANT_DYNAMIC_INVOKE = true;

	private final Class<? extends Enum<?>> en;
	private final int enSize;

	private final Class<?> enumSize;

	@SuppressWarnings("unchecked")
	public EnumDef(SerializerHandler<?, ?> handler, Clazz clazz) {
		super(handler, clazz);
		this.en = (Class<? extends Enum<?>>) clazz.getDefinedClass();
		this.enSize = this.en.getEnumConstants().length;

		if (this.enSize == 0) this.enumSize = null;
		else if (this.enSize <= 0xff) this.enumSize = byte.class;
		else if (this.enSize <= 0xffff) this.enumSize = short.class;
		else this.enumSize = int.class;
	}

	@SuppressWarnings({"unchecked", "unused"})
	public static <T extends Enum<T>> T[] getValues(MethodHandles.Lookup lookup, String name, Class<T[]> cls) {
		return (T[]) cls.componentType().getEnumConstants();
	}

	@Override
	protected void writeMethodGet(MethodHandler mh) {
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
			// mh.callInst(INVOKESTATIC, this.en, "values", this.en.arrayType());
			mh.visitFieldInsn(GETSTATIC, this.en, "VAL", this.en.arrayType());
		}
		mh.varOp(ILOAD, "io");
		mh.getIO(this.enumSize);
		mh.op(AALOAD);
	}

	@Override
	protected void writeMethodPut(MethodHandler mh, Runnable valueLoad) {
		mh.varOp(ILOAD, "io");
		valueLoad.run();
		mh.callInst(INVOKEVIRTUAL, Enum.class, "ordinal", int.class);
		mh.putIO(this.enumSize);
	}

	@Override
	public int getStaticSize() {
		return this.enumSize == null ? 0 : PrimitiveIODef.getSize(this.enumSize);
	}

	@Override
	public boolean hasDynamicSize() {
		return false;
	}

	@Override
	protected void writeMethodMeasure(MethodHandler mh, Runnable valueLoad) {
		throw new UnsupportedOperationException("Enums don't have a dynamic size");
	}
}
