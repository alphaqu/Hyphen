package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.annotations.Data;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class C1<A> extends C0 {
	@Data
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

	public static <A> Stream<? extends C1<A>> generate(Supplier<? extends Stream<? extends A>> aSupplier) {
		return aSupplier.get().map(C1::new);
	}

	@Override
	public String toString() {
		assert this.getClass() == C1.class;
		return "C1{" +
				"a=" + this.a +
				'}';
	}
}
