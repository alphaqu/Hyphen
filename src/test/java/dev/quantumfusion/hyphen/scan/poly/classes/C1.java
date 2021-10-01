package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.annotation.Serialize;

public class C1<A> extends C0 {
	@Serialize
	public A a;

	public C1(A a) {
		this.a = a;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		C1<?> c1 = (C1<?>) o;

		return this.a.equals(c1.a);
	}

	@Override
	public int hashCode() {
		return this.a.hashCode();
	}
}
