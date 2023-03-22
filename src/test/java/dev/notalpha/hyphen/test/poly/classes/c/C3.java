package dev.notalpha.hyphen.test.poly.classes.c;

import dev.notalpha.hyphen.util.TestSupplierUtil;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class C3<C, D> extends C2<C> {
    public D d;

    public C3(C c, C b1, D d) {
        super(c, b1);
        this.d = d;
    }

    public static <C, D> Supplier<? extends Stream<? extends C3<C, D>>> generateC3(
            Supplier<? extends Stream<? extends C>> cProvider,
            Supplier<? extends Stream<? extends D>> dProvider
    ) {
        return TestSupplierUtil.cross(cProvider, cProvider, dProvider, C3::new);
    }

    public static <C, D> Supplier<? extends Stream<? extends C3<C, D>>> generateC3Reduce(
            Supplier<? extends Stream<? extends C>> cProvider,
            Supplier<? extends Stream<? extends D>> dProvider,
            int seed) {
        return TestSupplierUtil.cross(
                TestSupplierUtil.reduce(cProvider, seed, 3),
                TestSupplierUtil.reduce(cProvider, seed * 31 + 5, 3),
                TestSupplierUtil.reduce(dProvider, seed * 127 - 7, 3), C3::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;

        C3<?, ?> c3 = (C3<?, ?>) o;

        return Objects.equals(this.d, c3.d);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(this.d.hashCode());
        return result;
    }

    @Override
    public String toString() {
        assert this.getClass() == C3.class;
        return "C3{" +
                "a=" + this.a +
                ", b=" + this.b +
                ", d=" + this.d +
                '}';
    }
}
