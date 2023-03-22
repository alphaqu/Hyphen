package dev.notalpha.hyphen.test.simple.arrays;

import dev.notalpha.hyphen.scan.annotations.DataNullable;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.notalpha.hyphen.util.TestSupplierUtil.*;

@TestThis
public class NullableIntArrayTest {
	public @DataNullable Integer[] data;

	public NullableIntArrayTest(Integer[] data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends NullableIntArrayTest>> generateNullableIntArrayTest() {
		return cross(array(nullable(INTEGERS), 125, 16, Integer.class), NullableIntArrayTest::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		NullableIntArrayTest that = (NullableIntArrayTest) o;
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
