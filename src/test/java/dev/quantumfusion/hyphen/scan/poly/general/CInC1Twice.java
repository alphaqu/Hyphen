package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C2;
import dev.quantumfusion.hyphen.util.TestSupplierUtil;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.INTEGERS;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

@Data
@TestThis
public class CInC1Twice {
	public final C1<@DataSubclasses({C1.class, C2.class}) C1<Integer>> data;
	public final C1<C1<Integer>> data2;

	public CInC1Twice(C1<C1<Integer>> data, C1<C1<Integer>> data2) {
		this.data = data;
		this.data2 = data2;
	}

	public static Supplier<? extends Stream<? extends CInC1Twice>> generateCInC1Twice() {
		return cross(C1.generateC1(TestSupplierUtil.subClasses(
						C1.generateC1(INTEGERS),
						C2.generateC2(INTEGERS)
				)),
				C1.generateC1(C1.generateC1(INTEGERS)),
				CInC1Twice::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		CInC1Twice that = (CInC1Twice) o;
		return Objects.equals(this.data, that.data) && Objects.equals(this.data2, that.data2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.data, this.data2);
	}

	@Override
	public String toString() {
		return "CInC1Twice{" +
				"data=" + this.data +
				", data2=" + this.data2 +
				'}';
	}
}
