package dev.quantumfusion.hyphen.scan.poly.classes.c.enums;

import dev.quantumfusion.hyphen.scan.poly.classes.c.CM1;

import java.util.function.Supplier;
import java.util.stream.Stream;

public enum EnumC implements CM1 {
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

    public static Supplier<Stream<? extends EnumC>> generateEnumC() {
        return () -> Stream.of(EnumC.values());
    }

    public static final EnumC[] VAL = values();
}
