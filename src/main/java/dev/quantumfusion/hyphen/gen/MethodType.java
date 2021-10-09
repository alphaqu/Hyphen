package dev.quantumfusion.hyphen.gen;

import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.util.ArrayUtil;

import java.util.function.BiFunction;

public enum MethodType {
	PUT(Param.VOID, Param.IO, Param.DATA),
	GET(Param.DATA, Param.IO),
	MEASURE(Param.MEASURE_VALUE, Param.DATA);


	public final String debugPrefix;
	public final Param returnClass;
	public final Param[] parameters;

	MethodType(Param returnClass, Param... parameters) {
		this.debugPrefix = this.name().toLowerCase();
		this.returnClass = returnClass;
		this.parameters = parameters;
	}

	public MethodInfo create(Clazz clazz, CodegenHandler ch) {
		String name = ch.debug ? debugPrefix : "" + clazz.toString();
		final Class<?> returnClass = this.returnClass.get(clazz, ch);
		final Class<?>[] parameters = ArrayUtil.map(this.parameters, Class[]::new, (param) -> param.get(clazz, ch));
		return new MethodInfo(name, returnClass, parameters);
	}

	public enum Param {
		VOID((c, ch) -> Void.TYPE),
		DATA((c, ch) -> c.pullBytecodeClass()),
		IO((c, ch) -> ch.ioClass),
		MEASURE_VALUE((c, ch) -> int.class);

		private final BiFunction<Clazz, CodegenHandler, Class<?>> getClass;

		Param(BiFunction<Clazz, CodegenHandler, Class<?>> getClass) {
			this.getClass = getClass;
		}

		public Class<?> get(Clazz c, CodegenHandler ch) {
			return getClass.apply(c, ch);
		}
	}
}
