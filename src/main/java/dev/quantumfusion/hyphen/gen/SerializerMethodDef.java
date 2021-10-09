package dev.quantumfusion.hyphen.gen;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.scan.type.Clazz;

public abstract class SerializerMethodDef implements SerializerDef {
	public final MethodInfo getInfo;
	public final MethodInfo putInfo;
	public final MethodInfo measureInfo;

	public SerializerMethodDef(SerializerHandler sh, MethodInfo getInfo, MethodInfo putInfo, MethodInfo measureInfo) {
		final CodegenHandler ch = sh.codegenHandler;

		this.getInfo = ch.apply(getInfo);
		this.putInfo = ch.apply(putInfo);
		this.measureInfo = ch.apply(measureInfo);
	}

	public SerializerMethodDef(SerializerHandler sh, Clazz clazz) {
		this(sh, MethodType.GET.create(clazz, sh.codegenHandler), MethodType.PUT.create(clazz, sh.codegenHandler), MethodType.MEASURE.create(clazz, sh.codegenHandler));
	}

	public abstract void writeGetMethod(MethodHandler mh);

	public abstract void writePutMethod(MethodHandler mh);

	public abstract void writeMeasureMethod(MethodHandler mh);
}
