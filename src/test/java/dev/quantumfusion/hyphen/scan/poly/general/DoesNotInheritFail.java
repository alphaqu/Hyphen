package dev.quantumfusion.hyphen.scan.poly.general;


import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C2;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.List;

@FailTest(/*IllegalInheritanceException.class*/)
@Data
@TestThis
public class DoesNotInheritFail {
	@DataSubclasses({C1.class, C2.class})
	public List<Integer> test;

	public DoesNotInheritFail(List<Integer> test) {
		this.test = test;
	}
}
