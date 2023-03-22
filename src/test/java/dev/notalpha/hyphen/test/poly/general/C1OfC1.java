package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.c.WrappedC1;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class C1OfC1 {
    @DataSubclasses({C1.class, WrappedC1.class})
    public C1<C1<Integer>> data;

    public C1OfC1(C1<C1<Integer>> data) {
        this.data = data;
    }

    public static Supplier<? extends Stream<? extends C1OfC1>> generateC1OfC1() {
        return TestSupplierUtil.cross(
                TestSupplierUtil.subClasses(
                        C1.generateC1(C1.generateC1(TestSupplierUtil.INTEGERS)),
                        WrappedC1.generateWrappedC1(C1.generateC1(TestSupplierUtil.INTEGERS))),
                C1OfC1::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        C1OfC1 c1OfC1 = (C1OfC1) o;
        return Objects.equals(this.data, c1OfC1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    @Override
    public String toString() {
        return "C1OfC1{" +
                "data=" + this.data +
                '}';
    }
}
