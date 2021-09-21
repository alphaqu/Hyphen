package dev.quantumfusion.hyphen.scan.poly;


import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.thr.exception.IllegalInheritanceException;

import java.util.List;

@FailTest(IllegalInheritanceException.class)
public class DoesNotInheritFail {
	@Serialize
	@SerSubclasses({C1.class, C2.class})
	public List<Integer> test;

	public DoesNotInheritFail(List<Integer> test) {
		this.test = test;
	}
}
