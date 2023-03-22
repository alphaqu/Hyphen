package dev.notalpha.hyphen.test.simple.map;


import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class MapSSTest {
    public final Map<String, String> data;

    public MapSSTest(Map<String, String> data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends MapSSTest>> generateMapSSTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.map(TestSupplierUtil.STRINGS, TestSupplierUtil.STRINGS, 6909, 16), MapSSTest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        MapSSTest mapIITest = (MapSSTest) o;
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
