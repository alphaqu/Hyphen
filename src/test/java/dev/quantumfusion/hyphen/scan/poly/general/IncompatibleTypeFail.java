package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.c.IntC1;
import dev.quantumfusion.hyphen.util.TestThis;


@FailTest(/*IncompatibleTypeException.class*/)
@Data
@TestThis
public class IncompatibleTypeFail {
	@DataSubclasses({C1.class, IntC1.class})
	public C1<Float> floatC1;


	public IncompatibleTypeFail(C1<Float> floatC1) {
		this.floatC1 = floatC1;
	}
}
