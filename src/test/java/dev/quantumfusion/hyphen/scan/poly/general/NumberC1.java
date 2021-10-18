package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.NUMBERS_IF;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

@Data
@TestThis
public class NumberC1 {
	public C1<@DataSubclasses({Integer.class, Float.class}) Object> data;

	public NumberC1(C1<Object> data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends NumberC>> generateNumberC() {
		return cross(C1.generateC1(NUMBERS_IF), NumberC::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		NumberC1 numberC1 = (NumberC1) o;
		return Objects.equals(this.data, numberC1.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.data);
	}

	@Override
	public String toString() {
		return "NumberC1{" +
				"data=" + this.data +
				'}';
	}
}
