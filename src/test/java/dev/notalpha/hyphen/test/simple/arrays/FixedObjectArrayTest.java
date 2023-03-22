package dev.notalpha.hyphen.test.simple.arrays;

import dev.notalpha.hyphen.scan.annotations.DataFixedArraySize;
import dev.notalpha.hyphen.test.simple.ObjectTest;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class FixedObjectArrayTest {
    public ObjectTest @DataFixedArraySize(16) [] objectArray;

    public FixedObjectArrayTest(ObjectTest[] objectArray) {
        this.objectArray = objectArray;
    }

    public static Supplier<Stream<? extends FixedObjectArrayTest>> generateFixedObjectArrayTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.array(ObjectTest.generateObjectTest(), 9852145, 30, 16, 16, ObjectTest.class), FixedObjectArrayTest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        FixedObjectArrayTest that = (FixedObjectArrayTest) o;
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
