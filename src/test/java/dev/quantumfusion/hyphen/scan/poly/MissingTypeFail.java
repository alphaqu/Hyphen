package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C3;
import dev.quantumfusion.hyphen.thr.MissingTypeInformationException;

@FailTest(MissingTypeInformationException.class)
public class MissingTypeFail {
	@Serialize
	@SerSubclasses({C1.class, C3.class})
	public C1<Integer> integer;


	public MissingTypeFail(C1<Integer> integer) {
		this.integer = integer;
	}
}
