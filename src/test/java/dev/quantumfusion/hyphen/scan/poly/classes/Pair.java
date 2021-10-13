package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.scan.annotations.Data;

public class Pair<A, B> implements IPair<A, B>, IReversedPair<B, A> {
	@Data
	public A a;
	@Data
	public B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
}
