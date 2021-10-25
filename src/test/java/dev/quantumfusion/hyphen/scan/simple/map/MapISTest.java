package dev.quantumfusion.hyphen.scan.simple.map;


import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@TestThis
@Data
public class MapISTest {
	@Data // FIXME
	public final Map<Integer, String> data;

	public MapISTest(Map<Integer, String> data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends MapISTest>> generateMapISTest() {
		return cross(map(INTEGERS, STRINGS, 6009, 16), MapISTest::new);
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
