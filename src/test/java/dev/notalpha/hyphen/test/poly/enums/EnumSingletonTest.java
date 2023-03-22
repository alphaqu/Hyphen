package dev.notalpha.hyphen.test.poly.enums;

import dev.notalpha.hyphen.test.poly.classes.c.enums.EnumCSingleton;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class EnumSingletonTest {
    public EnumCSingleton data;

    public EnumSingletonTest(EnumCSingleton data) {
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
        EnumSingletonTest c0IntC1 = (EnumSingletonTest) o;
        return Objects.equals(this.data, c0IntC1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    public static Supplier<Stream<? extends EnumSingletonTest>> generateEnumSingletonTest() {
        return TestSupplierUtil.cross(EnumCSingleton.generateEnumCSingleton(), EnumSingletonTest::new);
    }
}
