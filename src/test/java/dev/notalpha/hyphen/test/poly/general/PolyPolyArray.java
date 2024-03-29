package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class PolyPolyArray {
    // TODO: consider if this is type even makes sense
    public Number @DataSubclasses({Integer[].class, Float[].class}) [] numbers;

    public PolyPolyArray(Number[] numbers) {
        this.numbers = numbers;
    }

	public static Supplier<Stream<? extends PolyPolyArray>> generatePolyPolyArray() {
		return TestSupplierUtil.cross(TestSupplierUtil.subClasses(
				TestSupplierUtil.array(TestSupplierUtil.INTEGERS, 775865, 32, Integer.class),
				TestSupplierUtil.array(TestSupplierUtil.FLOATS, 54, 32, Float.class)
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
