package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.annotation.Serialize;

public class CoWrappedC1Extends<A, CA extends C1<? extends A>> extends C1<CA> {
	@Serialize
	public A selfA;

	public CoWrappedC1Extends(CA ca, A selfA) {
		super(ca);
		this.selfA = selfA;
	}

	// FIXME: Tracking issue #10
	public CoWrappedC1Extends(Object ca, A selfA) {
		this((CA) ca, selfA);
	}
}
