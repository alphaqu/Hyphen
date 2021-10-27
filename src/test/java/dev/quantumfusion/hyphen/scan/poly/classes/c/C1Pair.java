package dev.quantumfusion.hyphen.scan.poly.classes.c;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

public class C1Pair<A> extends C1<A> {
	public A second;

	public C1Pair(A a, A second) {
		super(a);
		this.second = second;
	}

	public static <A> Supplier<? extends Stream<? extends C1Pair<A>>> generateC1Pair(
			Supplier<? extends Stream<? extends A>> aProvider) {
		return cross(aProvider, aProvider, C1Pair::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!super.equals(o)) return false;

		C1Pair<?> c1Pair = (C1Pair<?>) o;

		return Objects.equals(this.second, c1Pair.second);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + Objects.hashCode(this.second);
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
