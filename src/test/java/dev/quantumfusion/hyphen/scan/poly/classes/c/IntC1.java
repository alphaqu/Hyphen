package dev.quantumfusion.hyphen.scan.poly.classes.c;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.INTEGERS;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

public class IntC1 extends C1<Integer> {
	public IntC1(Integer integer) {
		super(integer);
	}

	public static Supplier<? extends Stream<? extends IntC1>> generateIntC1() {
		return cross(INTEGERS, IntC1::new);
	}

	@Override
	public String toString() {
		return "IntC1{" +
				"a=" + this.a +
				'}';
	}
}
