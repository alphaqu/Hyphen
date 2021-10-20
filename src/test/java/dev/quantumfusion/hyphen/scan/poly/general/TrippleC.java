package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C2;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@Data
@TestThis
public class TrippleC {
	@DataSubclasses({C1.class, C2.class})
	public C1<@DataSubclasses({C1.class, C2.class}) C1<@DataSubclasses({C1.class, C2.class}) C1<Integer>>> data;

	public TrippleC(C1<C1<C1<Integer>>> data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends TrippleC>> generateTrippleC() {
		var inner = subClasses(C1.generateC1(INTEGERS), C2.generateC1(INTEGERS));
		var middle = subClasses(C1.generateC1(inner), C2.generateC1(inner));
		var outer = subClasses(C1.generateC1(middle), C2.generateC1(middle));
		return cross(outer, TrippleC::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		TrippleC trippleC = (TrippleC) o;
		return Objects.equals(this.data, trippleC.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.data);
	}

	@Override
	public String toString() {
		return "TrippleC{" +
				"data=" + this.data +
				'}';
	}
}
