package dev.notalpha.hyphen.test.poly.classes.c;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.notalpha.hyphen.util.TestSupplierUtil.cross;

public class WrappedC1<A> extends C1<C1<A>> {
    public WrappedC1(C1<A> ac1) {
        super(ac1);
    }

    public static <A> Supplier<? extends Stream<? extends WrappedC1<A>>> generateWrappedC1(
            Supplier<? extends Stream<? extends C1<A>>> ac1Supplier) {
        return cross(ac1Supplier, WrappedC1::new);
    }

    @Override
    public String toString() {
        assert this.getClass() == WrappedC1.class;
        return "WrappedC1{" +
                "a=" + this.a +
                '}';
    }
}
