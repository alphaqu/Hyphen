package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.*;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.subClasses;

@Data
@TestThis
public class EnumAnySubclassTest {
	@DataSubclasses({EnumC.class, EnumCBoolean.class, EnumCSingleton.class, C0.class, IntC1.class})
	public CM1 data;

	public EnumAnySubclassTest(CM1 data) {
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
		EnumAnySubclassTest c0IntC1 = (EnumAnySubclassTest) o;
		return Objects.equals(this.data, c0IntC1.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.data);
	}

	public static Supplier<Stream<? extends EnumAnySubclassTest>> generateEnumAnySubclassTest() {
		return cross(subClasses(
				EnumC.generateEnumC(),
				EnumCBoolean.generateEnumCBoolean(),
				EnumCSingleton.generateEnumCSingleton(),
				C0.generateC0(),
				IntC1.generateIntC1()
		), EnumAnySubclassTest::new);
	}
}
