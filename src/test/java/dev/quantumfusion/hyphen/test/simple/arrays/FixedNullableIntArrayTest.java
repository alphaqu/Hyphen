package dev.quantumfusion.hyphen.test.simple.arrays;

import dev.quantumfusion.hyphen.scan.annotations.DataFixedArraySize;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@TestThis
public class FixedNullableIntArrayTest {

	public 	@DataNullable Integer @DataFixedArraySize(2) [] data;

	public FixedNullableIntArrayTest(Integer[] data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends FixedNullableIntArrayTest>> generateFixedNullableIntArrayTest() {
		return cross(array(nullable(INTEGERS), 125, 2, 2, 2, Integer.class), FixedNullableIntArrayTest::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		FixedNullableIntArrayTest that = (FixedNullableIntArrayTest) o;
		return Arrays.equals(this.data, that.data);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.data);
	}

	@Override
	public String toString() {
		return "ArrayTest{" +
				"data=" + Arrays.toString(this.data) +
				'}';
	}
}
