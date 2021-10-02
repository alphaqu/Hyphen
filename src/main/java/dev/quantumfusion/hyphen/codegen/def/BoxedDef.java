package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

public class BoxedDef extends WrappedDef {
	public BoxedDef(Class<?> type, SerializerDef inner) {
		super(type, inner);
	}

	@Override
	protected void wrap(MethodHandler mh) {
		mh.callStaticMethod(this.getType(), "valueOf", this.getType(), this.inner.getType());
	}

	@Override
	protected void unwrap(MethodHandler mh) {
		mh.callInstanceMethod(this.getType(), this.inner.getType().getSimpleName() + "Value", this.inner.getType());
	}

	@Override
	public StringBuilder toFancyString(StringBuilder sb) {
		return sb.append("IO{").append(this.getType().getSimpleName()).append("}");
	}
}
