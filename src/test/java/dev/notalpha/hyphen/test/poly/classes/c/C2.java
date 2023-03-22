package dev.notalpha.hyphen.test.poly.classes.c;

import dev.notalpha.hyphen.util.TestSupplierUtil;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class C2<B> extends C1<B> {
    public B b;

    public C2(B b, B b1) {
        super(b);
        this.b = b1;
    }

    public static <B> Supplier<Stream<? extends C2<B>>> generateC2(
            Supplier<? extends Stream<? extends B>> bProvider) {
        return TestSupplierUtil.cross(bProvider, bProvider, C2::new);
    }

    public static <B> Supplier<Stream<? extends C2<B>>> generateC2Reduce(
            Supplier<? extends Stream<? extends B>> bProvider,
            int seed) {
        return TestSupplierUtil.cross(TestSupplierUtil.reduce(bProvider, seed), TestSupplierUtil.reduce(bProvider, 31 * seed + 5), C2::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;

        C2<?> c2 = (C2<?>) o;

        return this.b.equals(c2.b);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(this.b);
        return result;
    }

    @Override
    public String toString() {
        assert this.getClass() == C2.class;
        return "C2{" +
                "a=" + this.a +
                ", b=" + this.b +
                '}';
    }
}
