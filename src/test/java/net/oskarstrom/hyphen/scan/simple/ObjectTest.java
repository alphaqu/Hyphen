package net.oskarstrom.hyphen.scan.simple;

import net.oskarstrom.hyphen.Description;
import net.oskarstrom.hyphen.annotation.Serialize;

@Description("Tests if it can find a primitive and create a object impl")
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
