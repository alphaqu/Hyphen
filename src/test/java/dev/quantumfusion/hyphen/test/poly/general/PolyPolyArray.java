package dev.quantumfusion.hyphen.test.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.util.TestSupplierUtil;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@TestThis
public class PolyPolyArray {
    // TODO: consider if this is type even makes sense
    public Number @DataSubclasses({Integer[].class, Float[].class}) [] numbers;

    public PolyPolyArray(Number[] numbers) {
        this.numbers = numbers;
    }

	public static Supplier<Stream<? extends PolyPolyArray>> generatePolyPolyArray() {
		return cross(subClasses(
				array(INTEGERS, 775865, 32, Integer.class),
				array(FLOATS, 54, 32, Float.class)
		), PolyPolyArray::new);
	}

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
