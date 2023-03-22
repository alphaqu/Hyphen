package dev.notalpha.hyphen.test.simple.arrays;

import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class IntArrayTest {
    public int[] data;

    public IntArrayTest(int[] data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends IntArrayTest>> generateIntArrayTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.ints(75, 30, 0, 16), IntArrayTest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        IntArrayTest that = (IntArrayTest) o;
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
