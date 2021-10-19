package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.INTS;

@Data
@TestThis
public class PrimitiveTest {
	public int primitive;

	public PrimitiveTest(int primitive) {
		this.primitive = primitive;
	}


	public static Supplier<Stream<? extends PrimitiveTest>> generatePrimitiveTest() {
		return () -> INTS.get().mapToObj(PrimitiveTest::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		PrimitiveTest that = (PrimitiveTest) o;
		return this.primitive == that.primitive;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.primitive);
	}

	@Override
	public String toString() {
		return "PrimitiveTest{" +
				"primitive=" + this.primitive +
				'}';
	}
}
