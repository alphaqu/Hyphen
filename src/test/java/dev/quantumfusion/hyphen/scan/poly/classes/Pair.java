package net.oskarstrom.hyphen.scan.poly.classes;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.thr.IllegalClassException;

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
