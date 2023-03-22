package dev.notalpha.hyphen.test.poly.classes.c;

import dev.notalpha.hyphen.util.TestSupplierUtil;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CoWrappedC1Extends<A, CA extends C1<? extends A>> extends C1<CA> {
    public A selfA;

    public CoWrappedC1Extends(CA ca, A selfA) {
        super(ca);
        this.selfA = selfA;
    }

    public CoWrappedC1Extends(Object ca, A selfA) {
        super((CA) ca);
        this.selfA = selfA;
    }

    public static <A, CA extends C1<? extends A>> Supplier<? extends Stream<? extends CoWrappedC1Extends<A, CA>>> generateCoWrappedC1Extends(
            Supplier<? extends Stream<? extends A>> aSupplier,
            Supplier<? extends Stream<? extends CA>> caSupplier
    ) {
        return TestSupplierUtil.cross(caSupplier, aSupplier, CoWrappedC1Extends::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        CoWrappedC1Extends<?, ?> that = (CoWrappedC1Extends<?, ?>) o;
        return Objects.equals(this.selfA, that.selfA);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.selfA);
    }

    @Override
    public String toString() {
        return "CoWrappedC1Extends{" +
                "a=" + this.a +
                ", selfA=" + this.selfA +
                '}';
    }
}
