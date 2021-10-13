package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.type.ArrayClazz;
import dev.quantumfusion.hyphen.type.Clazz;
import dev.quantumfusion.hyphen.type.ParaClazz;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.List;

public class ScanHandler {
	public static final Clazz UNKNOWN = new Clazz(RuntimeException.class, null) {

		@Override
		public List<FieldEntry> getFields() {
			return List.of();
		}

		@Override
		public boolean equals(Object obj) {
			return obj == this;
		}

		@Override
		public int defined() {
			return 0;
		}

		@Override
		public String toString() {
			return "UNKNOWN";
		}
	};


	public static Clazz create(@NotNull Type type, @Nullable Clazz ctx, Direction dir) {
		return create(ScanUtil.wrap(type), ctx, dir);
	}

	public static Clazz create(@NotNull AnnotatedType annotatedType, @Nullable Clazz ctx, Direction dir) {
		if (annotatedType instanceof AnnotatedParameterizedType t) return ParaClazz.create(t, ctx, dir);
		if (annotatedType instanceof AnnotatedArrayType t) return ArrayClazz.createGeneric(t, ctx, dir);
		if (annotatedType instanceof AnnotatedTypeVariable t) {
			if (ctx == null) throw new RuntimeException("Type Knowledge Required");
			return ctx.define(t.getType().getTypeName());
		}
		if (annotatedType.getType() instanceof Class<?> t) {
			return Clazz.create(annotatedType, ctx, dir);
		}
		throw new RuntimeException("Can not handle: " + annotatedType.getClass().getSimpleName());
	}

}
