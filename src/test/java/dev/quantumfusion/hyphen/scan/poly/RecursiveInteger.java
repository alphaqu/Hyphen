package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.RecursiveC;
import dev.quantumfusion.hyphen.scan.poly.classes.TestSupplierUtil;

import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.scan.poly.classes.TestSupplierUtil.INTEGERS;

public class RecursiveInteger {
	@Serialize
	@SerSubclasses({C1.class, RecursiveC.class})
	public C1<Integer> data;

	public RecursiveInteger(C1<Integer> data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		RecursiveInteger recursive = (RecursiveInteger) o;

		return this.data.equals(recursive.data);
	}

	@Override
	public int hashCode() {
		return this.data.hashCode();
	}

	public static Stream<? extends RecursiveInteger> generate(
			int depth
	) {
		return TestSupplierUtil.<C1<Integer>>subClasses(
						() -> C1.generate(INTEGERS),
						() -> RecursiveC.generate(INTEGERS, depth - 1))
				.get()
				.map(RecursiveInteger::new);
	}

	@Override
	public String toString() {
		return "Recursive{" +
				"data=" + this.data +
				'}';
	}
}
