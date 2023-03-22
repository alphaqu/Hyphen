package dev.notalpha.hyphen.test.poly.enums;

import dev.notalpha.hyphen.scan.annotations.DataNullable;
import dev.notalpha.hyphen.test.poly.classes.c.enums.EnumCSingleton;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class EnumNullableSingletonTest {
    @DataNullable
    public EnumCSingleton data;

    public EnumNullableSingletonTest(EnumCSingleton data) {
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
        EnumNullableSingletonTest c0IntC1 = (EnumNullableSingletonTest) o;
        return Objects.equals(this.data, c0IntC1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    public static Supplier<Stream<? extends EnumNullableSingletonTest>> generateEnumNullableSingletonTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.nullable(EnumCSingleton.generateEnumCSingleton()), EnumNullableSingletonTest::new);
    }
}
