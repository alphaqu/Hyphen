package dev.quantumfusion.hyphen.scan.simple.arrays;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestSupplierUtil;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@Data
@TestThis
public class IntArrayArrayTest {
	public int[][] data;

	public IntArrayArrayTest(int[][] data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends IntArrayArrayTest>> generateIntArrayArrayTest() {
		return cross(array(ints(75, 10, 0, 16), 852, 16, int[].class), IntArrayArrayTest::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		IntArrayArrayTest that = (IntArrayArrayTest) o;
		return TestSupplierUtil.arrayDeepEquals(this.data, that.data);
	}

	@Override
	public int hashCode() {
		return TestSupplierUtil.arrayDeepHashCode(this.data);
	}

	@Override
	public String toString() {
		return "ArrayTest{" +
				"data=" + TestSupplierUtil.arrayToString(this.data) +
				'}';
	}
}
