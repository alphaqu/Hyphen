package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

public class ObjectTest {
	@Serialize
	public int primitive;

	@Serialize
	public PrimitiveTest object;

	public ObjectTest(int primitive, PrimitiveTest object) {
		this.primitive = primitive;
		this.object = object;
	}
}
