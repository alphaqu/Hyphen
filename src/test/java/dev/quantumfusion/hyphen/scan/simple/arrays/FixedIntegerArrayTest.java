package dev.quantumfusion.hyphen.scan.simple.arrays;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataFixedArraySize;
import dev.quantumfusion.hyphen.util.TestSupplierUtil;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@Data
@TestThis
public class FixedIntegerArrayTest {
	public Integer @DataFixedArraySize(16)[] data;

	public FixedIntegerArrayTest(Integer[] data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends FixedIntegerArrayTest>> generateFixedIntegerArrayTest() {
		return cross(array(INTEGERS, 125, 30, 16, 16, Integer.class), FixedIntegerArrayTest::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		FixedIntegerArrayTest that = (FixedIntegerArrayTest) o;
		return TestSupplierUtil.arrayDeepEquals(this.data, that.data);
	}

	@Override
	public int hashCode() {
		return TestSupplierUtil.arrayHashCode(this.data);
	}

	@Override
	public String toString() {
		return "ArrayTest{" +
				"data=" + TestSupplierUtil.arrayToString(this.data) +
				'}';
	}
}
