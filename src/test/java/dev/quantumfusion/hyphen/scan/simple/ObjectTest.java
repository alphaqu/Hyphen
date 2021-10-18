package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;

@Data
@TestThis
public class ObjectTest {
	public int primitive;
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
