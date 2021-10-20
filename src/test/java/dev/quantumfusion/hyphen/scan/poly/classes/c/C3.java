package dev.quantumfusion.hyphen.scan.poly.classes.c;

import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.reduce;


public class C3<C, D> extends C2<C> {
	@Data
	public D d;

	public C3(C c, C b1, D d) {
		super(c, b1);
		this.d = d;
	}

	public static <C, D> Supplier<? extends Stream<? extends C3<C, D>>> generateC3(
			Supplier<? extends Stream<? extends C>> cProvider,
			Supplier<? extends Stream<? extends D>> dProvider
	) {
		return cross(cProvider, cProvider, dProvider, C3::new);
	}

	public static <C, D> Supplier<? extends Stream<? extends C3<C, D>>> generateC3Reduce(
			Supplier<? extends Stream<? extends C>> cProvider,
			Supplier<? extends Stream<? extends D>> dProvider,
			int seed) {
		return cross(
				reduce(cProvider, seed, 3),
				reduce(cProvider, seed * 31 + 5, 3),
				reduce(dProvider, seed * 127 - 7, 3), C3::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!super.equals(o)) return false;

		C3<?, ?> c3 = (C3<?, ?>) o;

		return Objects.equals(this.d, c3.d);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + Objects.hashCode(this.d.hashCode());
		return result;
	}

	@Override
	public String toString() {
		assert this.getClass() == C3.class;
		return "C3{" +
				"a=" + this.a +
				", b=" + this.b +
				", d=" + this.d +
				'}';
	}
}
