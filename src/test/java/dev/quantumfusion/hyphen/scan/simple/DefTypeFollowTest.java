package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@Data
@TestThis
@FailTest // FIXME: should not fail
public class DefTypeFollowTest {
	public final Test test;

	public DefTypeFollowTest(Test test) {
		this.test = test;
	}

	public static Supplier<Stream<? extends DefTypeFollowTest>> generateDefTypeFollowTest() {
		return cross(array(INTEGERS, 98542, 32, Integer.class), arr -> {
					Test t = new Test();
					t.addAll(Arrays.asList(arr));
					return new DefTypeFollowTest(t);
				}
		);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		DefTypeFollowTest that = (DefTypeFollowTest) o;
		return Objects.equals(this.test, that.test);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.test);
	}

	@Override
	public String toString() {
		return "DefTypeFollowTest{" +
				"test=" + this.test +
				'}';
	}

	public static class Test extends ArrayList<Integer> {

	}
}
