package dev.quantumfusion.hyphen.test.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.NUMBERS_IF;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

@TestThis
public class NoGenerics {
    @DataSubclasses({Integer.class, Float.class})
    public Number number;


    public NoGenerics(Number number) {
        this.number = number;
    }

    public static Supplier<Stream<? extends NoGenerics>> generateNoGenerics() {
        return cross(NUMBERS_IF, NoGenerics::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        NoGenerics that = (NoGenerics) o;
        return Objects.equals(this.number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.number);
    }

    @Override
    public String toString() {
        return "NoGenerics{" +
                "number=" + this.number +
                '}';
    }
}
