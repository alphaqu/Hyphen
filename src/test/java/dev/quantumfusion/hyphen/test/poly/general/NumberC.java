package dev.quantumfusion.hyphen.test.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.test.poly.classes.c.C1;
import dev.quantumfusion.hyphen.test.poly.classes.c.C2;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@TestThis
public class NumberC {
    @DataSubclasses({C1.class, C2.class})
    public C1<@DataSubclasses({Integer.class, Float.class}) Number> data;

    public NumberC(C1<Number> data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends NumberC>> generateNumberC() {
        return cross(subClasses(
                C1.generateC1(NUMBERS_IF),
                C2.generateC1(NUMBERS_IF)
        ), NumberC::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        NumberC numberC = (NumberC) o;
        return Objects.equals(this.data, numberC.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    @Override
    public String toString() {
        return "NumberC{" +
                "data=" + this.data +
                '}';
    }
}
