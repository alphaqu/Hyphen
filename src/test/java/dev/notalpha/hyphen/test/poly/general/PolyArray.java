package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class PolyArray {
    public @DataSubclasses({Integer.class, Float.class}) Number[] numbers;

    public PolyArray(Number[] numbers) {
        this.numbers = numbers;
    }

    public static Supplier<Stream<? extends PolyArray>> generatePolyArray() {
        return TestSupplierUtil.cross(TestSupplierUtil.array(TestSupplierUtil.NUMBERS_IF, 78654, 32, Number.class), PolyArray::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        PolyArray polyArray = (PolyArray) o;
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
