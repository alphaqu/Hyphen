package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.test.poly.classes.c.C0;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class EmptyObjectTest {
    public C0 data;

    public EmptyObjectTest(C0 data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EnumTest{" +
                "data=" + this.data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        EmptyObjectTest c0IntC1 = (EmptyObjectTest) o;
        return Objects.equals(this.data, c0IntC1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    public static Supplier<Stream<? extends EmptyObjectTest>> generateEmptyObjectTest() {
        return TestSupplierUtil.cross(C0.generateC0(), EmptyObjectTest::new);
    }
}
