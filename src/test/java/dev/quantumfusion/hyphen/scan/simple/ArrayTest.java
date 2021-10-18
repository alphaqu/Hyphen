package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Arrays;

@Data
@TestThis
public class ArrayTest {
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
