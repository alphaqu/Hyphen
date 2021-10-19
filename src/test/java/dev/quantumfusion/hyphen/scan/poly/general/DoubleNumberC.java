package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@Data
@TestThis
public class DoubleNumberC {
	@DataSubclasses({C1.class, C2.class})
	public C1<@DataSubclasses({C1.class, C2.class}) C1<@DataSubclasses({Integer.class, Float.class}) Number>> data;

	public DoubleNumberC(C1<C1<Number>> data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends DoubleNumberC>> generateDoubleNumberC() {
		var sub = subClasses(
				C1.generateC1(NUMBERS_IF),
				C2.generateC2(NUMBERS_IF)
		);

		return cross(subClasses(C1.generateC1(sub), C2.generateC2Reduce(sub, 1011)), DoubleNumberC::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		DoubleNumberC that = (DoubleNumberC) o;
		return Objects.equals(this.data, that.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.data);
	}

	@Override
	public String toString() {
		return "DoubleNumberC{" +
				"data=" + this.data +
				'}';
	}
}
