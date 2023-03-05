package dev.quantumfusion.hyphen.test.poly.classes.pair;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

public class SelfPair<A> extends Pair<A, A> {
    public SelfPair(A a, A a2) {
        super(a, a2);
    }

    public static <A, B> Supplier<? extends Stream<? extends SelfPair<A>>> generateSelfPair(
            Supplier<? extends Stream<? extends A>> aProvider
    ) {
        return cross(aProvider, aProvider, SelfPair::new);
    }

    @Override
    public String toString() {
        return "SelfPair{" +
                "a=" + this.a +
                ", b=" + this.b +
                '}';
    }
}
