package dev.quantumfusion.hyphen.scan.poly.extract.wrapped;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1;
import dev.quantumfusion.hyphen.util.TestThis;

// Tracking issue #6
@FailTest(/*NotYetImplementedException.class*/)
@Data
@TestThis
public class ExtractExtendsC {
	@DataSubclasses({C1.class, CoWrappedC1.class})
	public C1<C2<Integer>> data;

	public ExtractExtendsC(C1<C2<Integer>> data) {
		this.data = data;
	}
}
