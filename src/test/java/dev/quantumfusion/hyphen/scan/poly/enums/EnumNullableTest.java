package dev.quantumfusion.hyphen.scan.poly.enums;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.poly.classes.c.enums.EnumC;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.nullable;

@Data
@TestThis
public class EnumNullableTest {
	@DataNullable
	public EnumC data;

	public EnumNullableTest(EnumC data) {
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
		EnumNullableTest c0IntC1 = (EnumNullableTest) o;
		return Objects.equals(this.data, c0IntC1.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.data);
	}

	public static Supplier<Stream<? extends EnumNullableTest>> generateEnumNullableTest() {
		return cross(nullable(EnumC.generateEnumC()), EnumNullableTest::new);
	}
}
