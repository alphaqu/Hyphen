package dev.notalpha.hyphen.test.simple.arrays;

import dev.notalpha.hyphen.scan.annotations.DataFixedArraySize;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class FixedStringArrayTest {
    public String @DataFixedArraySize(16) [] data;

    public FixedStringArrayTest(String[] data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends FixedStringArrayTest>> generateFixedStringArrayTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.array(TestSupplierUtil.STRINGS, 1215, 30, 16, 16, String.class), FixedStringArrayTest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        FixedStringArrayTest that = (FixedStringArrayTest) o;
        return TestSupplierUtil.arrayDeepEquals(this.data, that.data);
    }

    @Override
    public int hashCode() {
        return TestSupplierUtil.arrayDeepHashCode(this.data);
    }

    @Override
    public String toString() {
        return "ArrayTest{" +
                "data=" + TestSupplierUtil.arrayToString(this.data) +
                '}';
    }
}
