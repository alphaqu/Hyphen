package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.d.D1;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@TestThis
public class CInD1 {
    public D1<@DataSubclasses({C1.class, C2.class}) C1<Integer>> data;

    public CInD1(D1<C1<Integer>> data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends CInD1>> generateCInD1() {
        var sub = subClasses(
                C1.generateC1(INTEGERS),
                C2.generateC2(INTEGERS)
        );

        return cross(D1.generateD1(sub), CInD1::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        CInD1 cInD1 = (CInD1) o;
        return Objects.equals(this.data, cInD1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    @Override
    public String toString() {
        return "CInD1{" +
                "data=" + this.data +
                '}';
    }
}
