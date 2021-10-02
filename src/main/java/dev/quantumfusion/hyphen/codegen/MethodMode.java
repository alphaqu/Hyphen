package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.info.TypeInfo;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public enum MethodMode {
	GET("get", null, (info, ch) -> new Class[]{ch.getIOMode().ioClass}),
	PUT("put", Void.TYPE, (info, ch) -> new Class[]{ch.getIOMode().ioClass, info.getClazz()}),
	MEASURE("calcSub", long.class, (info, ch) -> new Class[]{info.getClazz()});

	public final String prefix;
	@Nullable // null if data
	public final Class<?> returnClass;

	public final BiFunction<TypeInfo, CodegenHandler, Class<?>[]> paramFunc;

	MethodMode(String prefix, @Nullable Class<?> returnClass, BiFunction<TypeInfo, CodegenHandler, Class<?>[]> paramFunc) {
		this.prefix = prefix;
		this.returnClass = returnClass;
		this.paramFunc = paramFunc;
	}
}
