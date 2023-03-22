package dev.notalpha.hyphen.test.poly.enums;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C0;
import dev.notalpha.hyphen.test.poly.classes.c.CM1;
import dev.notalpha.hyphen.test.poly.classes.c.IntC1;
import dev.notalpha.hyphen.test.poly.classes.c.enums.EnumC;
import dev.notalpha.hyphen.test.poly.classes.c.enums.EnumCBoolean;
import dev.notalpha.hyphen.test.poly.classes.c.enums.EnumCSingleton;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class EnumAnySubclassTest {
    @DataSubclasses({EnumC.class, EnumCBoolean.class, EnumCSingleton.class, C0.class, IntC1.class})
    public CM1 data;

    public EnumAnySubclassTest(CM1 data) {
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
        EnumAnySubclassTest c0IntC1 = (EnumAnySubclassTest) o;
        return Objects.equals(this.data, c0IntC1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    public static Supplier<Stream<? extends EnumAnySubclassTest>> generateEnumAnySubclassTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.subClasses(
                EnumC.generateEnumC(),
                EnumCBoolean.generateEnumCBoolean(),
                EnumCSingleton.generateEnumCSingleton(),
                C0.generateC0(),
                IntC1.generateIntC1()
        ), EnumAnySubclassTest::new);
    }
}
