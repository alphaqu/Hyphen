package dev.quantumfusion.hyphen.scan.poly.enums;

import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.poly.classes.c.enums.EnumCSingleton;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.nullable;

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
        return cross(nullable(EnumCSingleton.generateEnumCSingleton()), EnumNullableSingletonTest::new);
    }
}
