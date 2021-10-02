package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.codegen.FieldEntry;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.info.TypeInfo;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class GenUtil {

	public static String getMethodDesc(@Nullable Class<?> returnClazz, Class<?>... param) {
		Type returnType = returnClazz == null ? Type.VOID_TYPE : Type.getType(returnClazz);

		if (param.length == 0) return Type.getMethodDescriptor(returnType);
		Type[] params = new Type[param.length];
		for (int i = 0; i < param.length; i++)
			params[i] = Type.getType(param[i]);

		return Type.getMethodDescriptor(returnType, params);
	}

	public static String getVoidMethodDesc(Class<?>... param) {
		return getMethodDesc(Void.TYPE, param);
	}

	public static void castIfNotAssignable(MethodHandler mh, TypeInfo info) {
		if (!info.getClazz().isAssignableFrom(info.getRawType())) {
			mh.cast(info.getClazz());
		}
	}

	public static void getFieldFromClass(MethodHandler mh, TypeInfo owner, FieldEntry entry) {
		TypeInfo fieldType = entry.clazz();
		mh.getField(GETFIELD, owner.getClazz(), entry.name(), fieldType.getRawType());
		GenUtil.castIfNotAssignable(mh, fieldType);
	}

	public static void newDup(MethodHandler mh, TypeInfo clazz) {
		mh.typeInsn(NEW, clazz.getClazz());
		mh.visitInsn(DUP);
	}

	public static void addL(MethodHandler mh, long val) {
		mh.visitLdcInsn(val);
		mh.visitInsn(LADD);
	}

	public static void ifElseClass(MethodHandler mh, Class<?> target, MethodHandler.Var clazz, Runnable runnable) {
		Label skip = new Label();
		clazz.load();
		mh.visitLdcInsn(Type.getType(target));
		mh.visitJumpInsn(IF_ACMPNE, skip);
		runnable.run();
		mh.visitLabel(skip);
	}


	public static void nullCheckReturn(MethodHandler mh, MethodHandler.Var data, Runnable func) {
		Label nonNull = new Label();
		data.load();
		mh.visitJumpInsn(IFNONNULL, nonNull);
		func.run();
		mh.returnOp();
		mh.visitLabel(nonNull);
	}

	public static void load(MethodHandler.Var... vars) {
		for (MethodHandler.Var var : vars) var.load();
	}
}
