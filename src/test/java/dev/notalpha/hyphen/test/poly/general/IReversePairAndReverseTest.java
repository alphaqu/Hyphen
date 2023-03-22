package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.pair.IReversedPair;
import dev.notalpha.hyphen.test.poly.classes.pair.Pair;
import dev.notalpha.hyphen.test.poly.classes.pair.ReversePair;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class IReversePairAndReverseTest {
    @DataSubclasses({Pair.class, ReversePair.class})
    public IReversedPair<Integer, Float> data;


    public IReversePairAndReverseTest(IReversedPair<Integer, Float> data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends IReversePairAndReverseTest>> generateIReversePairAndReverseTest() {
        return TestSupplierUtil.cross(TestSupplierUtil.subClasses(Pair.generatePair(TestSupplierUtil.FLOATS, TestSupplierUtil.INTEGERS), ReversePair.generateReversePair(TestSupplierUtil.INTEGERS, TestSupplierUtil.FLOATS)), IReversePairAndReverseTest::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        IReversePairAndReverseTest that = (IReversePairAndReverseTest) o;
        return Objects.equals(this.data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.data);
    }

    @Override
    public String toString() {
        return "IReversePairAndReverseTest{" +
                "data=" + this.data +
                '}';
    }
}
