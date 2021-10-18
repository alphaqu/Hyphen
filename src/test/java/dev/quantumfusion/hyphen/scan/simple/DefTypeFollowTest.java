package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.ArrayList;
@Data
@TestThis
public class DefTypeFollowTest {
	public final Test test;

	public DefTypeFollowTest(Test test) {
		this.test = test;
	}

	public static class Test extends ArrayList<Integer> {

	}
}
