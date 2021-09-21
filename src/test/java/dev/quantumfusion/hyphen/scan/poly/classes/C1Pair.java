package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.annotation.Serialize;

public class C1Pair<A> extends C1<A> {
	@Serialize
	public A second;

	public C1Pair(A a, A second) {
		super(a);
		this.second = second;
	}
}
