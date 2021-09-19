package net.oskarstrom.hyphen.scan.poly.classes;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.thr.IllegalClassException;

@FailTest(IllegalClassException.class)
public class SelfPair<A> extends Pair<A, A> {
	public SelfPair(A a, A a2) {
		super(a, a2);
	}
}
