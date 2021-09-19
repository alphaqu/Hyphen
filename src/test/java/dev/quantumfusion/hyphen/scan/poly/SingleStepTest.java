package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;

public class SingleStepTest {
	@Serialize
	@SerSubclasses({C1.class, C2.class})
	public C1<Integer> integer;


	public SingleStepTest(C1<Integer> integer) {
		this.integer = integer;
	}
}
