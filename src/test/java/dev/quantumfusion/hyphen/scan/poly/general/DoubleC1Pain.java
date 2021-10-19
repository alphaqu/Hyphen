package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.*;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

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

	public static Supplier<? extends Stream<? extends DoubleC1Pain>> generateDoubleC1Pain(
	) {
		var supplier = subClasses(
				C1.generateC1(INTEGERS),
				C2.generateC2(INTEGERS),
				C3Def.generateC3Def(INTEGERS),
				C1Pair.generateC1(INTEGERS),
				IntC1.generateIntC1());

		return cross(subClasses(
				reduce(C1.generateC1(supplier), 5),
				reduce(C2.generateC2(supplier), 10),
				reduce(C3Def.generateC3Def(supplier), 20),
				reduce(C1Pair.generateC1Pair(supplier), 30)
		), DoubleC1Pain::new);
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
