package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

@TestThis
public class ObjectTest {
    public int primitive;
    public PrimitiveTest object;

    public ObjectTest(int primitive, PrimitiveTest object) {
        this.primitive = primitive;
        this.object = object;
    }

    public static Supplier<Stream<? extends ObjectTest>> generateObjectTest() {
        return cross(PrimitiveTest.generatePrimitiveTest(), PrimitiveTest.generatePrimitiveTest(), (a, b) -> new ObjectTest(a.primitive, b));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        ObjectTest that = (ObjectTest) o;
        return this.primitive == that.primitive && Objects.equals(this.object, that.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.primitive, this.object);
    }
}
