package net.oskarstrom.hyphen.scan.simple;

import net.oskarstrom.hyphen.Description;
import net.oskarstrom.hyphen.annotation.Serialize;

@Description("Tests if it inherits fields")
public class ExtendTest extends PrimitiveTest {
	@Serialize
	public ObjectTest objectTest;

	public ExtendTest(int primitive, ObjectTest objectTest) {
		super(primitive);
		this.objectTest = objectTest;
	}
}
