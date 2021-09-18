package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.Serialize;

public class C1<A> {
	@Serialize
	public A a;

	public C1(A a) {
		this.a = a;
	}
}
