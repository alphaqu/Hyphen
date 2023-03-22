package dev.notalpha.hyphen.test.simple.map;


import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class MapISTest {
    public final Map<Integer, String> data;

    public MapISTest(Map<Integer, String> data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends MapISTest>> generateMapISTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.map(TestSupplierUtil.INTEGERS, TestSupplierUtil.STRINGS, 6009, 16), MapISTest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        MapISTest mapIITest = (MapISTest) o;
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
