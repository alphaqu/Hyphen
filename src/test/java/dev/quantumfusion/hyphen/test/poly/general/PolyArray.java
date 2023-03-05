package dev.quantumfusion.hyphen.test.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.util.TestSupplierUtil;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@TestThis
public class PolyArray {
    public @DataSubclasses({Integer.class, Float.class}) Number[] numbers;

    public PolyArray(Number[] numbers) {
        this.numbers = numbers;
    }

    public static Supplier<Stream<? extends PolyArray>> generatePolyArray() {
        return cross(array(NUMBERS_IF, 78654, 32, Number.class), PolyArray::new);
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
