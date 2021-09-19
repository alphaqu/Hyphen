package net.oskarstrom.hyphen.scan.simple;

import net.oskarstrom.hyphen.annotation.Serialize;

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
