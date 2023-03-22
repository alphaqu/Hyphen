package dev.notalpha.hyphen.test.poly.enums;

import dev.notalpha.hyphen.FailTest;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
@FailTest(msg = "Enum does not contain any values")
public class EnumNullTestTest {
    public EnumNullTest data;

    public EnumNullTestTest(EnumNullTest data) {
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
        EnumNullTestTest c0IntC1 = (EnumNullTestTest) o;
        return Objects.equals(this.data, c0IntC1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    public static Supplier<Stream<? extends EnumNullTestTest>> generateEnumNullTestTest() {
        return TestSupplierUtil.cross(EnumNullTest.generateEnumNullTest(), EnumNullTestTest::new);
    }
}
