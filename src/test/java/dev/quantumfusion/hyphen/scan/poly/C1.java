package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.thr.IllegalClassException;

@FailTest(IllegalClassException.class)
public class C1<A> {
	@Serialize
	public A a;

	public C1(A a) {
		this.a = a;
	}
}
