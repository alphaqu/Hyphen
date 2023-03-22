package dev.notalpha.hyphen.test.simple.map;


import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class MapIITest {
    public final Map<Integer, Integer> data;

    public MapIITest(Map<Integer, Integer> data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends MapIITest>> generateMapIITest() {
        return TestSupplierUtil.cross(TestSupplierUtil.map(TestSupplierUtil.INTEGERS, TestSupplierUtil.INTEGERS, 6969, 16), MapIITest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        MapIITest mapIITest = (MapIITest) o;
        return Objects.equals(this.data, mapIITest.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    @Override
    public String toString() {
        return "MapIITest{" +
                "dataII=" + this.data +
                '}';
    }
}
