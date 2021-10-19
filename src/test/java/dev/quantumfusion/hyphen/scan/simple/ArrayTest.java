package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.ints;

@Data
@TestThis
public class ArrayTest {
	public int[] primativeArray;

	public ArrayTest(int[] primativeArray) {
		this.primativeArray = primativeArray;
	}

	public static Supplier<Stream<? extends ArrayTest>> generateArrayTest() {
		return cross(ints(75, 10, 0, 16), ArrayTest::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ArrayTest)) return false;
		ArrayTest arrayTest = (ArrayTest) o;
		return Arrays.equals(this.primativeArray, arrayTest.primativeArray);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.primativeArray);
	}

	@Override
	public String toString() {
		return "ArrayTest{" +
				"primativeArray=" + Arrays.toString(this.primativeArray) +
				'}';
	}
}
