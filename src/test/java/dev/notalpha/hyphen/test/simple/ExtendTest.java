package dev.notalpha.hyphen.test.simple;

import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class ExtendTest extends PrimitiveTest {
    public ObjectTest objectTest;

    public ExtendTest(int primitive, ObjectTest objectTest) {
        super(primitive);
        this.objectTest = objectTest;
    }


    public static Supplier<Stream<? extends ExtendTest>> generateExtendTest() {
        return TestSupplierUtil.cross(PrimitiveTest.generatePrimitiveTest(), ObjectTest.generateObjectTest(), (a, b) -> new ExtendTest(a.primitive, b));
    }
}
