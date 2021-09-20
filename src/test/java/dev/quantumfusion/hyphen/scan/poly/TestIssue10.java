package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C3Def;

public class TestIssue10 {
	@Serialize
	public C3Def<Float> floatC3Def;

	public TestIssue10(C3Def<Float> floatC3Def) {
		this.floatC3Def = floatC3Def;
	}
}
