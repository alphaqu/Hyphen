package dev.quantumfusion.hyphen.scan.poly.enums;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.poly.classes.c.enums.EnumCNull;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.nullable;

@Data
@TestThis
public class EnumNullableNullTest {
	@DataNullable
	public EnumCNull data;

	public EnumNullableNullTest(EnumCNull data) {
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
		EnumNullableNullTest c0IntC1 = (EnumNullableNullTest) o;
		return Objects.equals(this.data, c0IntC1.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.data);
	}

	public static Supplier<Stream<? extends EnumNullableNullTest>> generateEnumNullableNullTest() {
		return cross(nullable(EnumCNull.generateEnumCNull()), EnumNullableNullTest::new);
	}
}
