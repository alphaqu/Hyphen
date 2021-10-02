package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

import java.util.Arrays;

public class ObjectArrayTest {
	@Serialize
	public ObjectTest[] objectArray;

	public ObjectArrayTest(ObjectTest[] objectArray) {
		this.objectArray = objectArray;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ObjectArrayTest)) return false;
		ObjectArrayTest that = (ObjectArrayTest) o;
		return Arrays.equals(objectArray, that.objectArray);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(objectArray);
	}
}
