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
	public void doPut(MethodHandler mh) {
		// io data
		this.unwrap(mh);
		// io inner
		this.inner.doPut(mh);
		// --
	}

	@Override
	public void doGet(MethodHandler mh) {
		// io
		this.inner.doGet(mh);
		// inner
		this.wrap(mh);
		// data
	}
}
