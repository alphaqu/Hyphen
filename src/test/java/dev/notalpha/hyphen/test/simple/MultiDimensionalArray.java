package dev.notalpha.hyphen.test.simple;

import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class MultiDimensionalArray {
    public ObjectTest[][] whatAmIDoingInMyLife;

    public MultiDimensionalArray(ObjectTest[][] whatAmIDoingInMyLife) {
        this.whatAmIDoingInMyLife = whatAmIDoingInMyLife;
    }

    public static Supplier<Stream<? extends MultiDimensionalArray>> generateMultiDimensionalArray() {
        return TestSupplierUtil.cross(TestSupplierUtil.array(
                TestSupplierUtil.array(ObjectTest.generateObjectTest(), 9852145, 32, ObjectTest.class)
                , 23654, 32, ObjectTest[].class), MultiDimensionalArray::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        MultiDimensionalArray that = (MultiDimensionalArray) o;
        return TestSupplierUtil.arrayDeepEquals(this.whatAmIDoingInMyLife, that.whatAmIDoingInMyLife);
    }

    @Override
    public int hashCode() {
        return TestSupplierUtil.arrayDeepHashCode(this.whatAmIDoingInMyLife);
    }

    @Override
    public String toString() {
        return "MultiDimensionalArray{" +
                "whatAmIDoingInMyLife=" + TestSupplierUtil.arrayToString(this.whatAmIDoingInMyLife) +
                '}';
    }
}
