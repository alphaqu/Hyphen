package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

import java.util.Objects;

@Serialize
public class PrimitiveTest {
	public int primitive;

	public PrimitiveTest(int primitive) {
		this.primitive = primitive;
	}


	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (!(o instanceof PrimitiveTest)) return false;
		PrimitiveTest that = (PrimitiveTest) o;
		return primitive == that.primitive;
	}

	@Override
	public int hashCode() {
		return Objects.hash(primitive);
	}
}
