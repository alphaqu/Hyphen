package dev.notalpha.hyphen.test.poly.classes.pair;

import dev.notalpha.hyphen.util.TestSupplierUtil;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class IntPair<B> extends Pair<Integer, B> {
    public IntPair(Integer integer, B b) {
        super(integer, b);
    }

    public static <B> Supplier<? extends Stream<? extends IntPair<B>>> generateIntPair(
            Supplier<? extends Stream<? extends B>> bProvider) {
        return TestSupplierUtil.cross(TestSupplierUtil.INTEGERS, bProvider, IntPair::new);
    }

    @Override
    public String toString() {
        return "IntPair{" +
                "a=" + this.a +
                ", b=" + this.b +
                '}';
    }
}
