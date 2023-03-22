package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.c.C3Def;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.notalpha.hyphen.test.poly.classes.c.C1.generateC1;
import static dev.notalpha.hyphen.test.poly.classes.c.C3Def.generateC3Def;

@TestThis
public class UnknownInbetweenType {
    @DataSubclasses({C1.class, C3Def.class})
    public C1<Integer> integer;


    public UnknownInbetweenType(C1<Integer> integer) {
        this.integer = integer;
    }

    public static Supplier<Stream<? extends UnknownInbetweenType>> generateUnknownInbetweenType() {
        return TestSupplierUtil.cross(TestSupplierUtil.subClasses(
                generateC1(TestSupplierUtil.INTEGERS),
                generateC3Def(TestSupplierUtil.INTEGERS)
        ), UnknownInbetweenType::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnknownInbetweenType that = (UnknownInbetweenType) o;

        return Objects.equals(integer, that.integer);
    }

    @Override
    public int hashCode() {
        return integer != null ? integer.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UnknownInbetweenType{" +
                "integer=" + integer +
                '}';
    }
}
