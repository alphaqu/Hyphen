package net.oskarstrom.hyphen.scan.simple;

import net.oskarstrom.hyphen.annotation.Serialize;

public class ObjectArrayTest {
	@Serialize
	public ObjectTest[] objectArray;

	public ObjectArrayTest(ObjectTest[] objectArray) {
		this.objectArray = objectArray;
	}
}
