package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

public class PrimitiveTest {
	@Serialize
	public int primitive;

	public PrimitiveTest(int primitive) {
		this.primitive = primitive;
	}
}
