package net.oskarstrom.hyphen.scan.simple;

import net.oskarstrom.hyphen.Description;
import net.oskarstrom.hyphen.annotation.Serialize;

@Description("Tests if it can find a simple primitive")
public class PrimitiveTest {
	@Serialize
	public int primitive;

	public PrimitiveTest(int primitive) {
		this.primitive = primitive;
	}
}
