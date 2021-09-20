package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

public class ObjectArrayTest {
	@Serialize
	public ObjectTest[] objectArray;

	public ObjectArrayTest(ObjectTest[] objectArray) {
		this.objectArray = objectArray;
	}
}
