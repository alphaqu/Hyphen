package dev.quantumfusion.hyphen.scan.poly.extract.wrapped;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1;
import dev.quantumfusion.hyphen.thr.UnknownTypeException;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
// TODO: fix
@FailTest(UnknownTypeException.class)
public class ExtractOuterAnnotatedC {
	@DataSubclasses({C1.class, CoWrappedC1.class})
	public C1<@DataSubclasses({C1.class, C2.class}) C1<Integer>> data;

	public ExtractOuterAnnotatedC(C1<C1<Integer>> data) {
		this.data = data;
	}
}
