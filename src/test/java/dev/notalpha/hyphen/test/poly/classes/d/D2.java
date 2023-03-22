package dev.notalpha.hyphen.test.poly.classes.d;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.notalpha.hyphen.util.TestSupplierUtil.cross;

public class D2<B> extends D1<B> {
    public B b;

    public D2(B b, B b1) {
        super(b);
        this.b = b1;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;

        D2<?> d2 = (D2<?>) o;

        return Objects.equals(this.b, d2.b);
    }

    @Override
    public String toString() {
        return "D2{" +
                "a=" + this.a +
                ", b=" + this.b +
                '}';
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(this.b);
        return result;
    }

    public static <B> Supplier<Stream<? extends D2<B>>> generateD2(
            Supplier<? extends Stream<? extends B>> bProvider) {
        return cross(bProvider, bProvider, D2::new);
    }
}
