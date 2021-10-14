package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.scan.type.ArrayClazz;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.scan.type.ParaClazz;
import dev.quantumfusion.hyphen.scan.type.TypeClazz;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;

public class Clazzifier {
	public static Clazz create(@NotNull Type type, @Nullable Clazz ctx, Direction dir) {
		return create(ScanUtil.wrap(type), ctx, dir);
	}

	public static Clazz create(@NotNull AnnotatedType annotatedType, @Nullable Clazz ctx, Direction dir) {
		try {
			var type = annotatedType.getType();
			if (type instanceof ParameterizedType) return ParaClazz.create(annotatedType, ctx, dir);
			if (type instanceof GenericArrayType) return ArrayClazz.create(annotatedType, ctx, dir);
			if (type instanceof TypeVariable) return TypeClazz.create(annotatedType, ctx);
			if (type instanceof Class<?> c) if (c.isArray()) return ArrayClazz.create(annotatedType, ctx, dir);
			if (type instanceof Class<?>) return Clazz.create(annotatedType, ctx);
			throw new RuntimeException("Can not handle: " + annotatedType.getClass().getSimpleName());
		} catch (Throwable throwable) {
			throw HyphenException.thr(ctx, throwable);
		}
	}

}
