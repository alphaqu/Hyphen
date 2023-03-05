package dev.quantumfusion.hyphen.test.simple;

import dev.quantumfusion.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

@TestThis
public class ExtendTest extends PrimitiveTest {
    public ObjectTest objectTest;

    public ExtendTest(int primitive, ObjectTest objectTest) {
        super(primitive);
        this.objectTest = objectTest;
    }


    public static Supplier<Stream<? extends ExtendTest>> generateExtendTest() {
        return cross(PrimitiveTest.generatePrimitiveTest(), ObjectTest.generateObjectTest(), (a, b) -> new ExtendTest(a.primitive, b));
    }
}
