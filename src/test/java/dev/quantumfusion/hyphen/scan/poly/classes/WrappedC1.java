package dev.quantumfusion.hyphen.scan.poly.classes;

public class WrappedC1<A> extends C1<C1<A>> {
	public WrappedC1(C1<A> ac1) {
		super(ac1);
	}

	// FIXME: Tracking issue #10
	public WrappedC1(Object ac1) {
		this((C1<A>) ac1);
	}
}
