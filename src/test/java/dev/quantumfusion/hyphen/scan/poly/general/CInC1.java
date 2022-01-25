package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C2;
import dev.quantumfusion.hyphen.util.TestSupplierUtil;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.INTEGERS;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

@TestThis
public class CInC1 {
    public C1<@DataSubclasses({C1.class, C2.class}) C1<Integer>> data;

    public CInC1(C1<C1<Integer>> data) {
        this.data = data;
    }

    public static Supplier<? extends Stream<? extends CInC1>> generateCInC1() {
        return cross(C1.generateC1(TestSupplierUtil.subClasses(
                        C1.generateC1(INTEGERS),
                        C2.generateC2(INTEGERS)
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
