package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.type.ArrayClazz;
import dev.quantumfusion.hyphen.type.Clazz;
import dev.quantumfusion.hyphen.type.ParaClazz;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;

public class ScanHandler {
	public static final Clazz UNKNOWN = new Clazz(null) {
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


	public static Clazz create(@NotNull AnnotatedElement type, @Nullable Clazz ctx, Direction dir) {
		if (type instanceof AnnotatedParameterizedType t) return ParaClazz.create(t, ctx, dir);
		if (type instanceof AnnotatedArrayType t) return ArrayClazz.createGeneric(t, ctx, dir);
		if (type instanceof AnnotatedTypeVariable t) {
			if (ctx == null) throw new RuntimeException("Type Knowledge Required");
			return ctx.define(t.getType().getTypeName());
		}
		if (type instanceof Class<?> t) return Clazz.create(t, ctx, dir);
		throw new RuntimeException("Can not handle: " + type.getClass().getSimpleName());
	}
}
