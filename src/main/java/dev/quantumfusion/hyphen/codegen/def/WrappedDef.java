package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

public abstract class WrappedDef implements SerializerDef {
	private final Class<?> type;
	protected final SerializerDef inner;

	public WrappedDef(Class<?> type, SerializerDef inner) {
		this.type = type;
		this.inner = inner;
	}

	@Override
	public Class<?> getType() {
		return this.type;
	}

	protected abstract void wrap(MethodHandler mh);
	protected abstract void unwrap(MethodHandler mh);

	@Override
	public void writePut(MethodHandler mh) {
		// io data
		this.unwrap(mh);
		// io inner
		this.inner.writePut(mh);
		// --
	}

	@Override
	public void writeGet(MethodHandler mh) {
		// io
		this.inner.writeGet(mh);
		// inner
		this.wrap(mh);
		// data
	}


	@Override
	public boolean needsFieldOnMeasure() {
		return this.inner.needsFieldOnMeasure();
	}

	@Override
	public void writeMeasure(MethodHandler mh) {
		if (this.needsFieldOnMeasure())
			this.unwrap(mh);
		this.inner.writeMeasure(mh);
	}
}
