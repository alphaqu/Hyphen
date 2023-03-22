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
public class CInC1 {
    public C1<@DataSubclasses({C1.class, C2.class}) C1<Integer>> data;

    public CInC1(C1<C1<Integer>> data) {
        this.data = data;
    }

    public static Supplier<? extends Stream<? extends CInC1>> generateCInC1() {
        return TestSupplierUtil.cross(C1.generateC1(TestSupplierUtil.subClasses(
                        C1.generateC1(TestSupplierUtil.INTEGERS),
                        C2.generateC2(TestSupplierUtil.INTEGERS)
                )),
                CInC1::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        CInC1 cInC1 = (CInC1) o;
        return Objects.equals(this.data, cInC1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    @Override
    public String toString() {
        return "CInC1{" +
                "data=" + this.data +
                '}';
    }
}
