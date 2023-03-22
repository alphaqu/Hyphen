package dev.notalpha.hyphen.test.poly.classes.pair;

import dev.notalpha.hyphen.util.TestSupplierUtil;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Pair<A, B> implements IPair<A, B>, IReversedPair<B, A> {
    public A a;
    public B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public static <A, B> Supplier<? extends Stream<? extends Pair<A, B>>> generatePair(
            Supplier<? extends Stream<? extends A>> aProvider,
            Supplier<? extends Stream<? extends B>> bProvider
    ) {
        return TestSupplierUtil.cross(aProvider, bProvider, Pair::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(this.a, pair.a) && Objects.equals(this.b, pair.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.a, this.b);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "a=" + this.a +
                ", b=" + this.b +
                '}';
    }
}
