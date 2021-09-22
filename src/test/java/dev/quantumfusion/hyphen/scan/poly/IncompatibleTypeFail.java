package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.IntC1;
import dev.quantumfusion.hyphen.thr.exception.IncompatibleTypeException;


@FailTest(IncompatibleTypeException.class)
public class IncompatibleTypeFail {
	@Serialize
	@SerSubclasses({C1.class, IntC1.class})
	public C1<Float> floatC1;


	public IncompatibleTypeFail(C1<Float> floatC1) {
		this.floatC1 = floatC1;
	}
}
