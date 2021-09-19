package net.oskarstrom.hyphen.scan.poly.classes;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.thr.IllegalClassException;

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
