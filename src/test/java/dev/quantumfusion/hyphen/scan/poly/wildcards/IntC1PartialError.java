package dev.quantumfusion.hyphen.scan.poly.wildcards;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.IntC1;

@FailTest(NullPointerException.class)
//@FailTest(NotYetImplementedException.class)
public class IntC1PartialError {
	@Serialize
	@SerSubclasses({C1.class, IntC1.class})
	public C1<@SerSubclasses({Integer.class, Float.class})
			? extends Number> data;

	public IntC1PartialError(C1<? extends Number> data) {
		this.data = data;
	}
}
