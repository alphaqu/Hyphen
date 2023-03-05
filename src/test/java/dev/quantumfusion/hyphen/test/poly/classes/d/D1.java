package dev.quantumfusion.hyphen.test.poly.classes.d;

import dev.quantumfusion.hyphen.test.poly.classes.c.C0;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

public class D1<A> extends C0 {
    public A a;

    public D1(A a) {
        this.a = a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        D1<?> d1 = (D1<?>) o;

        return Objects.equals(this.a, d1.a);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.a);
    }

    @Override
    public String toString() {
        return "D1{" +
                "a=" + this.a +
                '}';
    }

    public static <A> Supplier<Stream<? extends D1<A>>> generateD1(
            Supplier<? extends Stream<? extends A>> aProvider) {
        return cross(aProvider, D1::new);
    }
}
