package dev.quantumfusion.hyphen.scan.simple.arrays;

import dev.quantumfusion.hyphen.scan.simple.ObjectTest;
import dev.quantumfusion.hyphen.util.TestSupplierUtil;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.array;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

@TestThis
public class ObjectArrayArrayTest {
    public ObjectTest[][] objectArray;

    public ObjectArrayArrayTest(ObjectTest[][] objectArray) {
        this.objectArray = objectArray;
    }

    public static Supplier<Stream<? extends ObjectArrayArrayTest>> generateObjectArrayArrayTest() {
        return cross(array(array(ObjectTest.generateObjectTest(), 98145, 32, ObjectTest.class), 12365, 16, ObjectTest[].class), ObjectArrayArrayTest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        ObjectArrayArrayTest that = (ObjectArrayArrayTest) o;
        return TestSupplierUtil.arrayDeepEquals(this.objectArray, that.objectArray);
    }

    @Override
    public int hashCode() {
        return TestSupplierUtil.arrayDeepHashCode(this.objectArray);
    }

    @Override
    public String toString() {
        return "ObjectArrayTest{" +
                "objectArray=" + TestSupplierUtil.arrayToString(this.objectArray) +
                '}';
    }
}
