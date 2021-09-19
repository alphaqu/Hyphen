package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.annotation.Serialize;

import java.util.ArrayList;

public class InheritImplTest {
	@Serialize
	public ArrayList<Integer> integers;

	public InheritImplTest(ArrayList<Integer> integers) {
		this.integers = integers;
	}

}
