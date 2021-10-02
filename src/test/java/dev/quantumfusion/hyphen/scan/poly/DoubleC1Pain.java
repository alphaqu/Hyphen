package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.*;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.scan.poly.classes.TestSupplierUtil.INTEGERS;

public class DoubleC1Pain {
	@Serialize
	@SerSubclasses({
			C1.class, C2.class, C3Def.class,
			C1Pair.class//, RecursiveC.class
	})
	public C1<
			@SerSubclasses({
					C1.class, C2.class, C3Def.class,
					C1Pair.class, IntC1.class//, RecursiveC.class
			}) C1<Integer>> data;

	public DoubleC1Pain(C1<C1<Integer>> data) {
		this.data = data;
	}

	public static Stream<? extends DoubleC1Pain> generate(
	) {
		Supplier<? extends Stream<? extends C1<Integer>>> supplier = TestSupplierUtil.<C1<Integer>>subClasses(
				() -> C1.generate(INTEGERS),
				() -> C2.generate(INTEGERS),
				() -> C3Def.generate(INTEGERS),
				() -> C1Pair.generate(INTEGERS),
				IntC1::generate);

		return TestSupplierUtil.<C1<C1<Integer>>>subClasses(
				() -> C1.generate(supplier),
				() -> C2.generate(supplier),
				() -> C3Def.generate(supplier),
				() -> C1Pair.generate(supplier)
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
