package dev.quantumfusion.hyphen.scan.poly.extract.wrapped;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1;
import dev.quantumfusion.hyphen.thr.exception.NotYetImplementedException;

@FailTest(NotYetImplementedException.class)
public class ExtractOuterAnnotatedC {
	@Serialize
	@SerSubclasses({C1.class, CoWrappedC1.class})
	public C1<@SerSubclasses({C1.class, C2.class}) C1<Integer>> data;

	public ExtractOuterAnnotatedC(C1<C1<Integer>> data) {
		this.data = data;
	}
}
