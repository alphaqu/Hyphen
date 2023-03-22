package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.pair.Pair;
import dev.notalpha.hyphen.test.poly.classes.pair.ReversePair;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class PairAndReverseTest {
    @DataSubclasses({Pair.class, ReversePair.class})
    public Pair<Integer, Float> data;


    public PairAndReverseTest(Pair<Integer, Float> data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends PairAndReverseTest>> generatePairAndReverseTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.subClasses(Pair.generatePair(TestSupplierUtil.INTEGERS, TestSupplierUtil.FLOATS), ReversePair.generateReversePair(TestSupplierUtil.FLOATS, TestSupplierUtil.INTEGERS)), PairAndReverseTest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        PairAndReverseTest that = (PairAndReverseTest) o;
        return Objects.equals(this.data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    @Override
    public String toString() {
        return "PairAndReverseTest{" +
                "data=" + this.data +
                '}';
    }
}
