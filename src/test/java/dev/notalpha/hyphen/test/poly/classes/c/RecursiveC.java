package dev.notalpha.hyphen.test.poly.classes.c;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.util.TestSupplierUtil;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RecursiveC<T> extends C1<T> {
    @DataSubclasses({C1.class, RecursiveC.class})
    public C1<T> foo;

    public RecursiveC(T t, C1<T> foo) {
        super(t);
        this.foo = foo;
    }

    public static <T> Supplier<? extends Stream<? extends RecursiveC<T>>> generateRecursiveC(
            Supplier<? extends Stream<? extends T>> tProvider,
            int depth
    ) {
        if (depth <= 0)
            return TestSupplierUtil.cross(
                    tProvider,
                    C1.<T>generateC1(tProvider),
                    RecursiveC::new);
        else
            return TestSupplierUtil.cross(
                    tProvider,
                    TestSupplierUtil.<C1<T>>subClasses(
                            C1.<T>generateC1(tProvider),
                            RecursiveC.generateRecursiveC(tProvider, depth - 1)
                    ),
                    RecursiveC::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        RecursiveC<?> that = (RecursiveC<?>) o;
        return Objects.equals(this.foo, that.foo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.foo);
    }

    @Override
    public String toString() {
        return "RecursiveC{" +
                "a=" + this.a +
                ", foo=" + this.foo +
                '}';
    }
}

