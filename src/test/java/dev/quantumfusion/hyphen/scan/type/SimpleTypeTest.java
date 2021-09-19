package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.annotation.Serialize;

import java.util.List;

public class SimpleTypeTest {
	@Serialize
	public List<Integer> integers;

	public SimpleTypeTest(List<Integer> integers) {
		this.integers = integers;
	}
}
