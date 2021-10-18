package dev.quantumfusion.hyphen.scan.poly.wildcards;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.IntC1;
import dev.quantumfusion.hyphen.util.TestThis;

@FailTest(NullPointerException.class)
@Data
@TestThis
//@FailTest(NotYetImplementedException.class)
public class IntC1PartialError {
	@DataSubclasses({C1.class, IntC1.class})
	public C1<@DataSubclasses({Integer.class, Float.class})
			? extends Number> data;

	public IntC1PartialError(C1<? extends Number> data) {
		this.data = data;
	}
}
