package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

public class ExtendTest extends PrimitiveTest {
	@Serialize
	public ObjectTest objectTest;

	public ExtendTest(int primitive, ObjectTest objectTest) {
		super(primitive);
		this.objectTest = objectTest;
	}
}
