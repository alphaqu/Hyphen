package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.util.TestSupplierUtil;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class PolyPolyArray {
	// TODO: consider if this is type even makes sense
	public @DataSubclasses({Integer.class, Float.class}) Number @DataSubclasses({Integer[].class, Float[].class, Number[].class}) [] numbers;

	public PolyPolyArray(Number[] numbers) {
		this.numbers = numbers;
	}
/*
	public static Supplier<Stream<? extends PolyPolyArray>> generatePolyPolyArray() {
		return cross(subClasses(
				array(INTEGERS, 775865, 32, Integer.class),
				array(FLOATS, 54, 32, Float.class),
				array(NUMBERS_IF, 78654, 32, Number.class)
		), PolyPolyArray::new);
	}*/

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		PolyPolyArray polyArray = (PolyPolyArray) o;
		return TestSupplierUtil.arrayEquals(this.numbers, polyArray.numbers);
	}

	@Override
	public int hashCode() {
		return TestSupplierUtil.arrayHashCode(this.numbers);
	}

	@Override
	public String toString() {
		return "PolyArray{" +
				"numbers=" + TestSupplierUtil.arrayToString(this.numbers) +
				'}';
	}
}
