package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.IPair;
import dev.quantumfusion.hyphen.scan.poly.classes.Pair;
import dev.quantumfusion.hyphen.scan.poly.classes.ReversePair;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@Data
@TestThis
public class IPairAndReverseTest {
	@DataSubclasses({Pair.class, ReversePair.class})
	public IPair<Integer, Float> data;


	public IPairAndReverseTest(IPair<Integer, Float> data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends IPairAndReverseTest>> generateIPairAndReverseTest() {
		return cross(subClasses(Pair.generatePair(INTEGERS, FLOATS), ReversePair.generateReversePair(FLOATS, INTEGERS)), IPairAndReverseTest::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		IPairAndReverseTest that = (IPairAndReverseTest) o;
		return Objects.equals(this.data, that.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.data);
	}

	@Override
	public String toString() {
		return "IPairAndReverseTest{" +
				"data=" + this.data +
				'}';
	}
}
