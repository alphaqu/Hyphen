package net.oskarstrom.hyphen.scan.simple;

import net.oskarstrom.hyphen.annotation.Serialize;

public class PrimitiveTest {
	@Serialize
	public int primitive;

	public PrimitiveTest(int primitive) {
		this.primitive = primitive;
	}
}
