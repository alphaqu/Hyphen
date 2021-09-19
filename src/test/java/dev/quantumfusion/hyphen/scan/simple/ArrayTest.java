package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

public class ArrayTest {
	@Serialize
	public int[] primativeArray;

	public ArrayTest(int[] primativeArray) {
		this.primativeArray = primativeArray;
	}
}
