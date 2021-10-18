package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class SingleStepTest {
	@DataSubclasses({C1.class, C2.class})
	public C1<Integer> integer;


	public SingleStepTest(C1<Integer> integer) {
		this.integer = integer;
	}
}
