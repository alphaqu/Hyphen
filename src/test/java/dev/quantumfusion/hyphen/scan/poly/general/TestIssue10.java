package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C3Def;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class TestIssue10 {
	public C3Def<Float> floatC3Def;

	public TestIssue10(C3Def<Float> floatC3Def) {
		this.floatC3Def = floatC3Def;
	}
}
