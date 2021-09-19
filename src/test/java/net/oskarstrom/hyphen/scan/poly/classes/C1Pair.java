package net.oskarstrom.hyphen.scan.poly.classes;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.thr.IllegalClassException;

@FailTest(IllegalClassException.class)
public class C1Pair<A> extends C1<A> {
	@Serialize
	public A second;

	public C1Pair(A a, A second){
		super(a);
		this.second = second;
	}
}
