package dev.quantumfusion.hyphen.test.poly.classes.pair;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

public class ReversePair<A, B> extends Pair<B, A> {
    public ReversePair(B b, A a) {
        super(b, a);
    }

    public static <A, B> Supplier<? extends Stream<? extends ReversePair<A, B>>> generateReversePair(
            Supplier<? extends Stream<? extends A>> aProvider,
            Supplier<? extends Stream<? extends B>> bProvider
    ) {
        return cross(bProvider, aProvider, ReversePair::new);
    }

    @Override
    public String toString() {
        return "ReversePair{" +
                "a=" + this.a +
                ", b=" + this.b +
                '}';
    }
}
