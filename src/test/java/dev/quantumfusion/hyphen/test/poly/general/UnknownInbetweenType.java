package dev.quantumfusion.hyphen.test.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.test.poly.classes.c.C1;
import dev.quantumfusion.hyphen.test.poly.classes.c.C3Def;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.test.poly.classes.c.C1.generateC1;
import static dev.quantumfusion.hyphen.test.poly.classes.c.C3Def.generateC3Def;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@TestThis
public class UnknownInbetweenType {
    @DataSubclasses({C1.class, C3Def.class})
    public C1<Integer> integer;


    public UnknownInbetweenType(C1<Integer> integer) {
        this.integer = integer;
    }

    public static Supplier<Stream<? extends UnknownInbetweenType>> generateUnknownInbetweenType() {
        return cross(subClasses(
                generateC1(INTEGERS),
                generateC3Def(INTEGERS)
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
