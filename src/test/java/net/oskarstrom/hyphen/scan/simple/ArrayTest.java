package net.oskarstrom.hyphen.scan.simple;

import net.oskarstrom.hyphen.annotation.Serialize;

public class ArrayTest {
	@Serialize
	public int[] primativeArray;

	public ArrayTest(int[] primativeArray) {
		this.primativeArray = primativeArray;
	}
}
