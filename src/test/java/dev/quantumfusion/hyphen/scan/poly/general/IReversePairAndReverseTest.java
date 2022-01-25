package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.pair.IReversedPair;
import dev.quantumfusion.hyphen.scan.poly.classes.pair.Pair;
import dev.quantumfusion.hyphen.scan.poly.classes.pair.ReversePair;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@TestThis
public class IReversePairAndReverseTest {
    @DataSubclasses({Pair.class, ReversePair.class})
    public IReversedPair<Integer, Float> data;


    public IReversePairAndReverseTest(IReversedPair<Integer, Float> data) {
        this.data = data;
    }

    public static Supplier<Stream<? extends IReversePairAndReverseTest>> generateIReversePairAndReverseTest() {
        return cross(subClasses(Pair.generatePair(FLOATS, INTEGERS), ReversePair.generateReversePair(INTEGERS, FLOATS)), IReversePairAndReverseTest::new);
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
