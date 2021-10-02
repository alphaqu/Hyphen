package dev.quantumfusion.hyphen.scan.poly.wildcards;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;

@FailTest(NullPointerException.class)
public class SimpleExtends {
	@Serialize
	@SerSubclasses({C1.class, C2.class})
	public C1<@SerSubclasses({Integer.class, Float.class})
			? extends Number> data;

	public SimpleExtends(C1<? extends Number> data) {
		this.data = data;
	}
}
