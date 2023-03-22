package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class Arrays {
    public Number @DataSubclasses({Integer[].class, Float[].class}) [] numbers;


    public Arrays(Number[] numbers) {
        this.numbers = numbers;
    }

    public static Supplier<Stream<? extends Arrays>> generateArrays() {
        return TestSupplierUtil.cross(TestSupplierUtil.subClasses(
                TestSupplierUtil.array(TestSupplierUtil.FLOATS, 54, 32, Float.class),
                TestSupplierUtil.array(TestSupplierUtil.INTEGERS, 775865, 32, Integer.class)
        ), Arrays::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Arrays arrays = (Arrays) o;
        return TestSupplierUtil.arrayEquals(this.numbers, arrays.numbers);
    }

    @Override
    public int hashCode() {
        return TestSupplierUtil.arrayHashCode(this.numbers);
    }

    @Override
    public String toString() {
        return "Arrays{" +
                "numbers=" + TestSupplierUtil.arrayToString(this.numbers) +
                '}';
    }
}