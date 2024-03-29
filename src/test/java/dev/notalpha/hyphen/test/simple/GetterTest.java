package dev.notalpha.hyphen.test.simple;

import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class GetterTest {
	public int x;
	private int y;

	public GetterTest(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static Supplier<Stream<? extends GetterTest>> generateGetterTest() {
		return TestSupplierUtil.cross(PrimitiveTest.generatePrimitiveTest(), PrimitiveTest.generatePrimitiveTest(), (a, b) -> new GetterTest(a.primitive, b.primitive));
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GetterTest that = (GetterTest) o;

		if (x != that.x) return false;
		return y == that.y;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		return result;
	}
}
