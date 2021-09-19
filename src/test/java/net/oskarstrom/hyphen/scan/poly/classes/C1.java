package net.oskarstrom.hyphen.scan.poly.classes;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.thr.IllegalClassException;

@FailTest(IllegalClassException.class)
public class C1<A> {
	@Serialize
	public A a;

	public C1(A a) {
		this.a = a;
	}
}
