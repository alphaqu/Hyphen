package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.scan.annotations.DataNullable;
import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.c.WrappedC1;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class NullableC1OfC1 {
    @Nullable
    @DataNullable
    @DataSubclasses({C1.class, WrappedC1.class})
    public C1<C1<Integer>> data;

    public NullableC1OfC1(@Nullable C1<C1<Integer>> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NullableC1OfC1)) return false;
        NullableC1OfC1 c1OfC1 = (NullableC1OfC1) o;
        return Objects.equals(data, c1OfC1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    public static Supplier<Stream<? extends NullableC1OfC1>> generateNullableC1OfC1() {
        return TestSupplierUtil.cross(TestSupplierUtil.nullableSubClasses(
                C1.generateC1(C1.generateC1(TestSupplierUtil.INTEGERS)),
                WrappedC1.generateWrappedC1(C1.generateC1(TestSupplierUtil.INTEGERS))
        ), NullableC1OfC1::new);
    }

    @Override
    public String toString() {
        return "NullableC1OfC1{" +
                "data=" + this.data +
                '}';
    }
}
