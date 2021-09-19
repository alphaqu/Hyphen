package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.thr.IllegalClassException;

@FailTest(IllegalClassException.class)
public class Pair<A, B> implements IPair<A,B>, IReversedPair<B,A> {
	@Serialize
	public A a;
	@Serialize
	public B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
}
