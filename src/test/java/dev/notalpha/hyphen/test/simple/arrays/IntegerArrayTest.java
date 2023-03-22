package dev.notalpha.hyphen.test.simple.arrays;

import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class IntegerArrayTest {
    public Integer[] data;

    public IntegerArrayTest(Integer[] data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends IntegerArrayTest>> generateIntegerArrayTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.array(TestSupplierUtil.INTEGERS, 125, 16, Integer.class), IntegerArrayTest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        IntegerArrayTest that = (IntegerArrayTest) o;
        return TestSupplierUtil.arrayDeepEquals(this.data, that.data);
    }

    @Override
    public int hashCode() {
        return TestSupplierUtil.arrayHashCode(this.data);
    }

    @Override
    public String toString() {
        return "ArrayTest{" +
                "data=" + TestSupplierUtil.arrayToString(this.data) +
                '}';
    }
}
