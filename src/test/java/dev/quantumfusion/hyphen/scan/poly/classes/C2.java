package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.annotation.Serialize;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class C2<B> extends C1<B> {
	@Serialize
	public B b;

	public C2(B b, B b1) {
		super(b);
		this.b = b1;
	}

	public static <B> Stream<? extends C2<B>> generate(Supplier<? extends Stream<? extends B>> bSupplier){
		return bSupplier.get().flatMap(b -> bSupplier.get().map(b1 -> new C2<>(b,b1)));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		C2<?> c2 = (C2<?>) o;

		return this.b.equals(c2.b);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + this.b.hashCode();
		return result;
	}

	@Override
	public String toString() {
		assert this.getClass() == C2.class;
		return "C2{" +
				"a=" + this.a +
				", b=" + this.b +
				'}';
	}
}
