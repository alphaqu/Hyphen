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
public class TrippleC {
    @DataSubclasses({C1.class, C2.class})
    public C1<@DataSubclasses({C1.class, C2.class}) C1<@DataSubclasses({C1.class, C2.class}) C1<Integer>>> data;

    public TrippleC(C1<C1<C1<Integer>>> data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends TrippleC>> generateTrippleC() {
        var inner = TestSupplierUtil.subClasses(C1.generateC1(TestSupplierUtil.INTEGERS), C2.generateC1(TestSupplierUtil.INTEGERS));
        var middle = TestSupplierUtil.subClasses(C1.generateC1(inner), C2.generateC1(inner));
        var outer = TestSupplierUtil.subClasses(C1.generateC1(middle), C2.generateC1(middle));
        return TestSupplierUtil.cross(outer, TrippleC::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        TrippleC trippleC = (TrippleC) o;
        return Objects.equals(this.data, trippleC.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    @Override
    public String toString() {
        return "TrippleC{" +
                "data=" + this.data +
                '}';
    }
}
