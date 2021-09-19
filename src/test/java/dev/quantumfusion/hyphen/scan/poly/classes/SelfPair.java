package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.thr.IllegalClassException;

@FailTest(IllegalClassException.class)
public class SelfPair<A> extends Pair<A, A> {
	public SelfPair(A a, A a2) {
		super(a, a2);
	}
}
