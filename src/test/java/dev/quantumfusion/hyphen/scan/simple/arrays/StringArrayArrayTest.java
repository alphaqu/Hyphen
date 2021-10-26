package dev.quantumfusion.hyphen.scan.simple.arrays;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestSupplierUtil;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@Data
@TestThis
public class StringArrayArrayTest {
	public String[][] data;

	public StringArrayArrayTest(String[][] data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends StringArrayArrayTest>> generateStringArrayArrayTest() {
		return cross(array(array(STRINGS, 100125, 16, String.class), 6952, 16, String[].class), StringArrayArrayTest::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		StringArrayArrayTest that = (StringArrayArrayTest) o;
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
