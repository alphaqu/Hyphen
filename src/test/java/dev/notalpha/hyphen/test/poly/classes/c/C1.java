package dev.notalpha.hyphen.test.poly.classes.c;

import dev.notalpha.hyphen.util.TestSupplierUtil;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class C1<A> extends C0 {
    public A a;

    public C1(A a) {
        this.a = a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        C1<?> c1 = (C1<?>) o;

        return Objects.equals(this.a, c1.a);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.a);
    }

    public static <A> Supplier<Stream<? extends C1<A>>> generateC1(Supplier<? extends Stream<? extends A>> aProvider) {
        return TestSupplierUtil.cross(aProvider, C1::new);
    }

    @Override
    public String toString() {
        assert this.getClass() == C1.class;
        return "C1{" +
                "a=" + this.a +
                '}';
    }
}
