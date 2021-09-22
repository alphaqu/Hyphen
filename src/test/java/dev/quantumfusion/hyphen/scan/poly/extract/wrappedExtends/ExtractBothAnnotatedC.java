package dev.quantumfusion.hyphen.scan.poly.extract.wrappedExtends;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1Extends;
import dev.quantumfusion.hyphen.thr.exception.NotYetImplementedException;

@FailTest(NotYetImplementedException.class)
public class ExtractBothAnnotatedC {
	@Serialize
	@SerSubclasses({C1.class, CoWrappedC1Extends.class})
	public C1<@SerSubclasses({C1.class, C2.class}) C1<@SerSubclasses({Float.class, Integer.class}) Number>> data;

	public ExtractBothAnnotatedC(C1<C1<Number>> data) {
		this.data = data;
	}
}
