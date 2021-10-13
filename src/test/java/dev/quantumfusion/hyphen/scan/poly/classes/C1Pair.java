package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class C1Pair<A> extends C1<A> {
	@Data
	public A second;

	public C1Pair(A a, A second) {
		super(a);
		this.second = second;
	}

	public static <A> Stream<? extends C1Pair<A>> generate(Supplier<? extends Stream<? extends A>> aSupplier){
		return aSupplier.get().flatMap(a -> aSupplier.get().map(b -> new C1Pair<>(a,b)));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		C1Pair<?> c1Pair = (C1Pair<?>) o;

		return this.second.equals(c1Pair.second);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + this.second.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "C1Pair{" +
				"a=" + this.a +
				", second=" + this.second +
				'}';
	}
}
