package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.SerializerHandler;
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
	public static Clazz create(SerializerHandler<?, ?> handler, @NotNull Type type, @Nullable Clazz ctx, Direction dir) {
		return create(handler, ScanUtil.wrap(type), ctx, dir);
	}

	public static Clazz create(SerializerHandler<?, ?> handler, @NotNull AnnotatedType annotatedType, @Nullable Clazz ctx, Direction dir) {
		try {
			var type = annotatedType.getType();
			if (type instanceof ParameterizedType) {
				return ParaClazz.create(handler, annotatedType, ctx, dir);
			}
			if (type instanceof GenericArrayType) {
				return ArrayClazz.create(handler, annotatedType, ctx, dir);
			}
			if (type instanceof WildcardType) {
				return Clazzifier.create(handler, ((AnnotatedWildcardType) annotatedType).getAnnotatedUpperBounds()[0], ctx, dir);
			}
			if (type instanceof TypeVariable) {
				return TypeClazz.create(handler, annotatedType, ctx);
			}
			if (type instanceof Class<?> c && c.getTypeParameters().length > 0) {
				return ParaClazz.create(handler, annotatedType, ctx, dir);
			}
			if (type instanceof Class<?> c && c.isArray()) {
				return ArrayClazz.create(handler, annotatedType, ctx, dir);
			}
			if (type instanceof Class<?>) {
				return Clazz.create(handler, annotatedType, ctx);
			}
			throw new RuntimeException("Can not handle: " + annotatedType.getClass().getSimpleName());
		} catch (Throwable throwable) {
			Class<?> classFrom = ScanUtil.getClassFrom(annotatedType);
			throw HyphenException.rethrow(Clazz.create(classFrom),null, throwable);
		}
	}

}
