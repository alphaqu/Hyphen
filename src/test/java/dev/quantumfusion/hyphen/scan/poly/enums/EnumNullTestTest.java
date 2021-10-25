package dev.quantumfusion.hyphen.scan.poly.enums;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

@Data
@TestThis
@FailTest(msg = "Enum does not contain any values")
public class EnumNullTestTest {
	public EnumNullTest data;

	public EnumNullTestTest(EnumNullTest data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "EnumTest{" +
				"data=" + this.data +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		EnumNullTestTest c0IntC1 = (EnumNullTestTest) o;
		return Objects.equals(this.data, c0IntC1.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.data);
	}

	public static Supplier<Stream<? extends EnumNullTestTest>> generateEnumNullTestTest() {
		return cross(EnumNullTest.generateEnumNullTest(), EnumNullTestTest::new);
	}
}
