package dev.quantumfusion.hyphen.test.poly.general;

import dev.quantumfusion.hyphen.test.poly.classes.c.C3Def;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.test.poly.classes.c.C3Def.generateC3Def;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@TestThis
public class TestIssue10 {
    public C3Def<Float> floatC3Def;

    public TestIssue10(C3Def<Float> floatC3Def) {
        this.floatC3Def = floatC3Def;
    }

    public static Supplier<Stream<? extends TestIssue10>> generateTestIssue10() {
        return cross(generateC3Def(FLOATS), TestIssue10::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestIssue10 that = (TestIssue10) o;

        return Objects.equals(floatC3Def, that.floatC3Def);
    }

    @Override
    public int hashCode() {
        return floatC3Def != null ? floatC3Def.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TestIssue10{" +
                "floatC3Def=" + floatC3Def +
                '}';
    }
}
