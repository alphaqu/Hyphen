package dev.notalpha.hyphen.test.simple;

import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class PrimitiveTest {
    public int primitive;

    public PrimitiveTest(int primitive) {
        this.primitive = primitive;
    }


    public static Supplier<Stream<? extends PrimitiveTest>> generatePrimitiveTest() {
        return () -> TestSupplierUtil.INTS.get().mapToObj(PrimitiveTest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        PrimitiveTest that = (PrimitiveTest) o;
        return this.primitive == that.primitive;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.primitive);
    }

    @Override
    public String toString() {
        return "PrimitiveTest{" +
                "primitive=" + this.primitive +
                '}';
    }
}
