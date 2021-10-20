package dev.quantumfusion.hyphen.scan.poly.classes.c;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

public class C3Ignore<C, D> extends C2<C> {
	public D d;

	public C3Ignore(C c, C b1) {
		super(c, b1);
	}

	public static <C, D> Supplier<? extends Stream<? extends C3Ignore<C, D>>> generateC3Ignore(
			Supplier<? extends Stream<? extends C>> cProvider
	) {
		return cross(cProvider, cProvider, C3Ignore::new);
	}

	@Override
	public String toString() {
		return "C3Ignore{" +
				"a=" + this.a +
				", b=" + this.b +
				", d=" + this.d +
				'}';
	}
}
