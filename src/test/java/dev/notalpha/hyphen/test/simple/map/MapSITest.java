package dev.notalpha.hyphen.test.simple.map;


import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class MapSITest {
    public final Map<String, Integer> data;

    public MapSITest(Map<String, Integer> data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends MapSITest>> generateMapSITest() {
        return TestSupplierUtil.cross(TestSupplierUtil.map(TestSupplierUtil.STRINGS, TestSupplierUtil.INTEGERS, 6069, 16), MapSITest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        MapSITest mapIITest = (MapSITest) o;
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
