package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.thr.IllegalClassException;

@FailTest(IllegalClassException.class)
public class CoWrappedC1<A, CA extends C1<A>> extends C1<CA>{
	@Serialize
	public A selfA;

	public CoWrappedC1(CA ca, A selfA) {
		super(ca);
		this.selfA = selfA;
	}

	// FIXME: Tracking issue #10
	public CoWrappedC1(Object ca, A selfA){
		this((CA) ca, selfA);
	}
}
