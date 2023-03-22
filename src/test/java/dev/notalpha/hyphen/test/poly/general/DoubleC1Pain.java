package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.*;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class DoubleC1Pain {
    @DataSubclasses({
            C1.class, C2.class, C3Def.class,
            C1Pair.class//, RecursiveC.class
    })
    public C1<
            @DataSubclasses({
                    C1.class, C2.class, C3Def.class,
                    C1Pair.class, IntC1.class//, RecursiveC.class
            }) C1<Integer>> data;

    public DoubleC1Pain(C1<C1<Integer>> data) {
        this.data = data;
    }

    public static Supplier<? extends Stream<? extends DoubleC1Pain>> generateDoubleC1Pain(
    ) {
        var supplier = TestSupplierUtil.subClasses(
                C1.generateC1(TestSupplierUtil.INTEGERS),
                C2.generateC2(TestSupplierUtil.INTEGERS),
                C3Def.generateC3Def(TestSupplierUtil.INTEGERS),
                TestSupplierUtil.reduce(C1Pair.generateC1(TestSupplierUtil.INTEGERS), 50),
                IntC1.generateIntC1());

        // FIXME: look into hangup
        return TestSupplierUtil.cross(TestSupplierUtil.subClasses(
                TestSupplierUtil.reduce(C1.generateC1(supplier), 5),
                //reduce(C3Def.generateC3Def(supplier), 20),
                TestSupplierUtil.reduce(C2.generateC2(supplier), 10)
                //reduce(C1Pair.generateC1Pair(supplier), 30)
        ), DoubleC1Pain::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        DoubleC1Pain that = (DoubleC1Pain) o;

        return this.data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return this.data.hashCode();
    }

    @Override
    public String toString() {
        return "DoubleC1Pain{" +
                "data=" + this.data +
                '}';
    }
}
