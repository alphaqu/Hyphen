package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

@Serialize
public class PrimitiveTest {
	public int primitive;

	public PrimitiveTest(int primitive) {
		this.primitive = primitive;
	}
}
