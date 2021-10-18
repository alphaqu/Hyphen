package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.*;
import dev.quantumfusion.hyphen.util.TestSupplierUtil;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.INTEGERS;

@Data
@TestThis
public class DoubleC1Pain {
	@DataSubclasses({
			C1.class, C2.class, C3Def.class,
			C1Pair.class//, RecursiveC.class
	})
	public C1<
			@DataSubclasses({
					C1.class, C2.class, C3Def.class,
					C1Pair.class, IntC1.class//, RecursiveC.class
			}) C1<Integer>> data;

	public DoubleC1Pain(C1<C1<Integer>> data) {
		this.data = data;
	}

	public static Stream<? extends DoubleC1Pain> generateDoubleC1Pain(
	) {
		Supplier<? extends Stream<? extends C1<Integer>>> supplier = TestSupplierUtil.<C1<Integer>>subClasses(
				C1.generateC1(INTEGERS),
				C2.generateC2(INTEGERS),
				C3Def.generateC3Def(INTEGERS),
				C1Pair.generateC1(INTEGERS),
				IntC1.generateIntC1());

		return TestSupplierUtil.<C1<C1<Integer>>>subClasses(
				C1.generateC1(supplier),
				C2.generateC2(supplier),
				C3Def.generateC3Def(supplier),
				C1Pair.generateC1Pair(supplier)
		).get().map(DoubleC1Pain::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		DoubleC1Pain that = (DoubleC1Pain) o;

		return this.data.equals(that.data);
	}

	@Override
	public int hashCode() {
		return this.data.hashCode();
	}

	@Override
	public String toString() {
		return "DoubleC1Pain{" +
				"data=" + this.data +
				'}';
	}
}
