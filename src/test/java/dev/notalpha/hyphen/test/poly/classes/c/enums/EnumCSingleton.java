package dev.notalpha.hyphen.test.poly.classes.c.enums;

import dev.notalpha.hyphen.test.poly.classes.c.CM1;

import java.util.function.Supplier;
import java.util.stream.Stream;

public enum EnumCSingleton implements CM1 {
    SINGLETON;

    public static Supplier<Stream<? extends EnumCSingleton>> generateEnumCSingleton() {
        return () -> Stream.of(EnumCSingleton.values());
    }
}
