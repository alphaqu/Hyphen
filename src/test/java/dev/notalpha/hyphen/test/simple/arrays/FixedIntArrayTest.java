package dev.notalpha.hyphen.test.simple.arrays;

import dev.notalpha.hyphen.scan.annotations.DataFixedArraySize;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class FixedIntArrayTest {
    public int @DataFixedArraySize(16) [] data;

    public FixedIntArrayTest(int[] data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends FixedIntArrayTest>> generateFixedIntArrayTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.ints(75, 30, 16, 16), FixedIntArrayTest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        FixedIntArrayTest that = (FixedIntArrayTest) o;
        return Arrays.equals(this.data, that.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    @Override
    public String toString() {
        return "ArrayTest{" +
                "data=" + Arrays.toString(this.data) +
                '}';
    }
}
