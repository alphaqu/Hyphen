package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.Pair;
import dev.quantumfusion.hyphen.scan.poly.classes.ReversePair;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@Data
@TestThis
public class PairAndReverseTest {
	@DataSubclasses({Pair.class, ReversePair.class})
	public Pair<Integer, Float> data;


	public PairAndReverseTest(Pair<Integer, Float> data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends PairAndReverseTest>> generatePairAndReverseTest() {
		return cross(subClasses(Pair.generatePair(INTEGERS, FLOATS), ReversePair.generateReversePair(FLOATS, INTEGERS)), PairAndReverseTest::new);
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
