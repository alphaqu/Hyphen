package dev.notalpha.hyphen.test.poly.classes.pair;

import dev.notalpha.hyphen.util.TestSupplierUtil;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class SelfPair<A> extends Pair<A, A> {
    public SelfPair(A a, A a2) {
        super(a, a2);
    }

    public static <A, B> Supplier<? extends Stream<? extends SelfPair<A>>> generateSelfPair(
            Supplier<? extends Stream<? extends A>> aProvider
    ) {
        return TestSupplierUtil.cross(aProvider, aProvider, SelfPair::new);
    }

    @Override
    public String toString() {
        return "SelfPair{" +
                "a=" + this.a +
                ", b=" + this.b +
                '}';
    }
}
