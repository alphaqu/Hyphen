package dev.quantumfusion.hyphen.scan.poly.classes;

import java.util.stream.Stream;

public class IntC1 extends C1<Integer> {
	public IntC1(Integer integer) {
		super(integer);
	}

	// FIXME: tracking issue #10
	public IntC1(Object integer) {
		super((Integer) integer);
	}

	public static Stream<? extends IntC1> generate(){
		return TestSupplierUtil.INTEGERS.get().map(IntC1::new);
	}
}
