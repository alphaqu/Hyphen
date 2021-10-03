package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.info.TypeInfo;

import java.util.function.BiFunction;

public enum Vars {
	DATA((codegenHandler, info) -> info.getClazz()),
	IO((codegenHandler, info) -> codegenHandler.getIOMode().ioClass);

	public final BiFunction<CodegenHandler, TypeInfo, Class<?>> classGetter;

	Vars(BiFunction<CodegenHandler, TypeInfo, Class<?>> classGetter) {
		this.classGetter = classGetter;
	}

	public void createVar(MethodHandler mh, CodegenHandler ch, TypeInfo info) {
		mh.createVar(name().toLowerCase(), classGetter.apply(ch, info));
	}

	public MethodHandler.Var get(MethodHandler mh) {
		return mh.getVar(name().toLowerCase());
	}
}
