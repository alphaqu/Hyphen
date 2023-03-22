package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.FailTest;
import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.c.RecursiveC;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
@FailTest // FIXME infinite toString()
public class RecursiveInteger {
	@DataSubclasses({C1.class, RecursiveC.class})
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

	public static Supplier<? extends Stream<? extends RecursiveInteger>> generateRecursiveInteger(
			int depth
	) {
		return TestSupplierUtil.cross(TestSupplierUtil.subClasses(
						C1.generateC1(TestSupplierUtil.INTEGERS),
						RecursiveC.generateRecursiveC(TestSupplierUtil.INTEGERS, depth - 1))
				, RecursiveInteger::new);
	}

	@Override
	public String toString() {
		return "Recursive{" +
				"data=" +
				'}';
	}
}
