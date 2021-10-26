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
public class FixedStringArrayTest {
	public String @DataFixedArraySize(16)[] data;

	public FixedStringArrayTest(String[] data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends FixedStringArrayTest>> generateFixedStringArrayTest() {
		return cross(array(STRINGS, 1215,  10, 16,16, String.class), FixedStringArrayTest::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		FixedStringArrayTest that = (FixedStringArrayTest) o;
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
