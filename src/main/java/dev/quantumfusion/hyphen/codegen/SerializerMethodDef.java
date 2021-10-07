package dev.quantumfusion.hyphen.codegen;

public abstract class SerializerMethodDef implements SerializerDef {
	public final MethodInfo getInfo;
	public final MethodInfo putInfo;
	public final MethodInfo measureInfo;

	public SerializerMethodDef(CodegenHandler handler) {
		this.getInfo = handler.apply(getMethodInfo());
		this.putInfo = handler.apply(putMethodInfo());
		this.measureInfo = handler.apply(measureMethodInfo());
	}

	public abstract void writeGetMethod(MethodHandler mh);

	public abstract void writePutMethod(MethodHandler mh);

	public abstract void writeMeasureMethod(MethodHandler mh);

	public abstract MethodInfo getMethodInfo();

	public abstract MethodInfo putMethodInfo();

	public abstract MethodInfo measureMethodInfo();
}
