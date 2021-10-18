package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class ExtendTest extends PrimitiveTest {
	public ObjectTest objectTest;

	public ExtendTest(int primitive, ObjectTest objectTest) {
		super(primitive);
		this.objectTest = objectTest;
	}
}
