package dev.quantumfusion.hyphen.test.poly.classes.c.enums;

import dev.quantumfusion.hyphen.test.poly.classes.c.CM1;

import java.util.function.Supplier;
import java.util.stream.Stream;

public enum EnumCNull implements CM1 {
    ;

    public static Supplier<Stream<? extends EnumCNull>> generateEnumCNull() {
        return Stream::empty;
    }
}
