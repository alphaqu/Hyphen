package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class NumberC1 {
    public C1<@DataSubclasses({Integer.class, Float.class}) Object> data;

    public NumberC1(C1<Object> data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends NumberC1>> generateNumberC1() {
        return TestSupplierUtil.cross(C1.generateC1(TestSupplierUtil.NUMBERS_IF), NumberC1::new);
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
