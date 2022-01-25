package dev.quantumfusion.hyphen.scan.poly.classes.pair;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.INTEGERS;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

public class IntPair<B> extends Pair<Integer, B> {
    public IntPair(Integer integer, B b) {
        super(integer, b);
    }

    public static <B> Supplier<? extends Stream<? extends IntPair<B>>> generateIntPair(
            Supplier<? extends Stream<? extends B>> bProvider) {
        return cross(INTEGERS, bProvider, IntPair::new);
    }

    @Override
    public String toString() {
        return "IntPair{" +
                "a=" + this.a +
                ", b=" + this.b +
                '}';
    }
}
