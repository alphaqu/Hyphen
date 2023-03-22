package dev.notalpha.hyphen.test.poly.enums;

import dev.notalpha.hyphen.scan.annotations.DataNullable;
import dev.notalpha.hyphen.test.poly.classes.c.enums.EnumC;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class EnumNullableTest {
    @DataNullable
    public EnumC data;

    public EnumNullableTest(EnumC data) {
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
        EnumNullableTest c0IntC1 = (EnumNullableTest) o;
        return Objects.equals(this.data, c0IntC1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    public static Supplier<Stream<? extends EnumNullableTest>> generateEnumNullableTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.nullable(EnumC.generateEnumC()), EnumNullableTest::new);
    }
}
