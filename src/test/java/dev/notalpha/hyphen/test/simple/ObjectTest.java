package dev.notalpha.hyphen.test.simple;

import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class ObjectTest {
    public int primitive;
    public PrimitiveTest object;

    public ObjectTest(int primitive, PrimitiveTest object) {
        this.primitive = primitive;
        this.object = object;
    }

    public static Supplier<Stream<? extends ObjectTest>> generateObjectTest() {
        return TestSupplierUtil.cross(PrimitiveTest.generatePrimitiveTest(), PrimitiveTest.generatePrimitiveTest(), (a, b) -> new ObjectTest(a.primitive, b));
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
