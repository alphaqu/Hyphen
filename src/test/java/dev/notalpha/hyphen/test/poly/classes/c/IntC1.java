package dev.notalpha.hyphen.test.poly.classes.c;

import dev.notalpha.hyphen.util.TestSupplierUtil;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class IntC1 extends C1<Integer> {
    public IntC1(Integer integer) {
        super(integer);
    }

    public static Supplier<? extends Stream<? extends IntC1>> generateIntC1() {
        return TestSupplierUtil.cross(TestSupplierUtil.INTEGERS, IntC1::new);
    }

    @Override
    public String toString() {
        return "IntC1{" +
                "a=" + this.a +
                '}';
    }
}
