package dev.notalpha.hyphen.test.poly.enums;

import dev.notalpha.hyphen.scan.annotations.DataNullable;
import dev.notalpha.hyphen.test.poly.classes.c.enums.EnumCBoolean;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class EnumNullableBooleanTest {
    @DataNullable
    public EnumCBoolean data;

    public EnumNullableBooleanTest(EnumCBoolean data) {
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
        EnumNullableBooleanTest c0IntC1 = (EnumNullableBooleanTest) o;
        return Objects.equals(this.data, c0IntC1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    public static Supplier<Stream<? extends EnumNullableBooleanTest>> generateEnumNullableBooleanTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.nullable(EnumCBoolean.generateEnumCBoolean()), EnumNullableBooleanTest::new);
    }
}
