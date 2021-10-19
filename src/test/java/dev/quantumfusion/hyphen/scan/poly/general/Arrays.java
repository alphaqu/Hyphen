package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@Data
@TestThis
public class Arrays {
	public Number @DataSubclasses({Integer[].class, Float[].class}) [] numbers;


	public Arrays(Number[] numbers) {
		this.numbers = numbers;
	}

	public static Supplier<Stream<? extends Arrays>> generateArrays() {
		return cross(subClasses(
				array(FLOATS, 54, 32, Float.class),
				array(INTEGERS, 775865, 32, Integer.class)
		), Arrays::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		Arrays arrays = (Arrays) o;
		return arrayEquals(this.numbers, arrays.numbers);
	}

	@Override
	public int hashCode() {
		return arrayHashCode(this.numbers);
	}

	@Override
	public String toString() {
		return "Arrays{" +
				"numbers=" + arrayToString(this.numbers) +
				'}';
	}
}