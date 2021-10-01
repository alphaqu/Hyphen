package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

import java.util.Arrays;

public class ArrayTest {
	@Serialize
	public int[] primativeArray;

	public ArrayTest(int[] primativeArray) {
		this.primativeArray = primativeArray;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ArrayTest)) return false;
		ArrayTest arrayTest = (ArrayTest) o;
		return Arrays.equals(primativeArray, arrayTest.primativeArray);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(primativeArray);
	}
}
