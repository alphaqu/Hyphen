package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C2;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@TestThis
public class DoubleC {
    @DataSubclasses({C1.class, C2.class})
    public C1<@DataSubclasses({C1.class, C2.class}) C1<Integer>> data;

    public DoubleC(C1<C1<Integer>> data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends DoubleC>> generateDoubleC() {
        var sub = subClasses(
                C1.generateC1(INTEGERS),
                C2.generateC2(INTEGERS)
        );

        return cross(subClasses(C1.generateC1(sub), C2.generateC2(sub)), DoubleC::new);
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
