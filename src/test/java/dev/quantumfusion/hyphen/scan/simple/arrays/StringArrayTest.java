package dev.quantumfusion.hyphen.scan.simple.arrays;

import dev.quantumfusion.hyphen.util.TestSupplierUtil;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@TestThis
public class StringArrayTest {
    public String[] data;

    public StringArrayTest(String[] data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends StringArrayTest>> generateStringArrayTest() {
        return cross(array(STRINGS, 125, 16, String.class), StringArrayTest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        StringArrayTest that = (StringArrayTest) o;
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
