package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

import java.util.Objects;

public class ObjectTest {
	@Serialize
	public int primitive;

	@Serialize
	public PrimitiveTest object;

	public ObjectTest(int primitive, PrimitiveTest object) {
		this.primitive = primitive;
		this.object = object;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ObjectTest)) return false;
		ObjectTest that = (ObjectTest) o;
		return primitive == that.primitive && Objects.equals(object, that.object);
	}

	@Override
	public int hashCode() {
		return Objects.hash(primitive, object);
	}
}
