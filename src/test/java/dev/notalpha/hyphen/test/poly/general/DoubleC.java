package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.c.C2;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class DoubleC {
    @DataSubclasses({C1.class, C2.class})
    public C1<@DataSubclasses({C1.class, C2.class}) C1<Integer>> data;

    public DoubleC(C1<C1<Integer>> data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends DoubleC>> generateDoubleC() {
        var sub = TestSupplierUtil.subClasses(
                C1.generateC1(TestSupplierUtil.INTEGERS),
                C2.generateC2(TestSupplierUtil.INTEGERS)
        );

        return TestSupplierUtil.cross(TestSupplierUtil.subClasses(C1.generateC1(sub), C2.generateC2(sub)), DoubleC::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        DoubleC doubleC = (DoubleC) o;
        return Objects.equals(this.data, doubleC.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    @Override
    public String toString() {
        return "DoubleC{" +
                "data=" + this.data +
                '}';
    }
}
