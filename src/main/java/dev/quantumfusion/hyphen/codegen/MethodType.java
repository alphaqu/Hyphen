package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;

import java.util.function.BiConsumer;

public enum MethodType {
	GET(Vars.DATA, Options.DISABLE_GET, MethodMetadata::writeGet, Vars.IO),
	PUT(Void.TYPE, Options.DISABLE_PUT, MethodMetadata::writePut, Vars.IO, Vars.DATA),
	MEASURE(long.class, Options.DISABLE_MEASURE, MethodMetadata::writeMeasure, Vars.DATA);

	public final Object returnClass;
	public final Vars[] parameters;
	public final Options disableOption;
	public final BiConsumer<MethodMetadata<?>, MethodHandler> writer;

	MethodType(Object returnClass, Options disableOption, BiConsumer<MethodMetadata<?>, MethodHandler> writer, Vars... parameters) {
		this.returnClass = returnClass;
		this.disableOption = disableOption;
		this.writer = writer;
		this.parameters = parameters;
	}

	public Class<?>[] getParameters(CodegenHandler ch, TypeInfo info) {
		Class<?>[] out = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			out[i] = getClazzFromVar(parameters[i], ch, info);
		}
		return out;
	}

	public Class<?> getReturn(CodegenHandler ch, TypeInfo info) {
		if (returnClass instanceof Class<?> c) return c;
		if (returnClass instanceof Vars vars) return getClazzFromVar(vars, ch, info);
		throw new RuntimeException("?");
	}

	public String parseMethodName(TypeInfo info) {
		return this.name().toLowerCase() + info.getMethodName(false);
	}

	private Class<?> getClazzFromVar(Vars var, CodegenHandler ch, TypeInfo info) {
		if (var == Vars.IO) return ch.getIOMode().ioClass;
		if (var == Vars.DATA) return info.getClazz();
		else throw new RuntimeException(var + " ??");
	}

}
