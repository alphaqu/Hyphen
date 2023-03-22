package dev.notalpha.hyphen.test.poly.classes.c.enums;

import dev.notalpha.hyphen.test.poly.classes.c.CM1;

import java.util.function.Supplier;
import java.util.stream.Stream;

public enum EnumCBoolean implements CM1 {
    TRUE, FALSE;

    public static Supplier<Stream<? extends EnumCBoolean>> generateEnumCBoolean() {
        return () -> Stream.of(EnumCBoolean.values());
    }
}
