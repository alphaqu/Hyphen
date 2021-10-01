package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.annotation.Serialize;

import java.util.Objects;

public class C1<A> extends C0 {
	@Serialize
	public A a;

	public C1(A a) {
		this.a = a;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof C1)) return false;
		C1<?> c1 = (C1<?>) o;
		return Objects.equals(a, c1.a);
	}

	@Override
	public int hashCode() {
		return Objects.hash(a);
	}
}
