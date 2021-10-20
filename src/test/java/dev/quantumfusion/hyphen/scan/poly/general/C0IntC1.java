package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C0;
import dev.quantumfusion.hyphen.scan.poly.classes.c.IntC1;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.subClasses;

@Data
@TestThis
public class C0IntC1 {
	@DataSubclasses({C0.class, IntC1.class})
	public C0 data;

	public C0IntC1(C0 data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "C0IntC1{" +
				"data=" + this.data +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		C0IntC1 c0IntC1 = (C0IntC1) o;
		return Objects.equals(this.data, c0IntC1.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.data);
	}

	public static Supplier<? extends Stream<? extends C0IntC1>> generateC0IntC1() {
		return cross(subClasses(C0.generateC0(), IntC1.generateIntC1()), C0IntC1::new);
	}
}
