package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C3;
import dev.quantumfusion.hyphen.util.TestThis;

@FailTest(/*UnknownTypeException.class*/)
@Data
@TestThis
public class MissingTypeFail {
	@DataSubclasses({C1.class, C3.class})
	public C1<Integer> integer;


	public MissingTypeFail(C1<Integer> integer) {
		this.integer = integer;
	}
}
