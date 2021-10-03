package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

import java.util.ArrayList;

public class DefTypeFollowTest {
	@Serialize
	public final Test test;

	public DefTypeFollowTest(Test test) {
		this.test = test;
	}

	public static class Test extends ArrayList<Integer> {

	}
}
