package dev.notalpha.hyphen.test.simple.arrays;

import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class IntegerArrayArrayTest {
    public Integer[][] data;

    public IntegerArrayArrayTest(Integer[][] data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends IntegerArrayArrayTest>> generateIntegerArrayArrayTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.array(TestSupplierUtil.array(TestSupplierUtil.INTEGERS, 1250, 16, Integer.class), 254, 16, Integer[].class), IntegerArrayArrayTest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        IntegerArrayArrayTest that = (IntegerArrayArrayTest) o;
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
