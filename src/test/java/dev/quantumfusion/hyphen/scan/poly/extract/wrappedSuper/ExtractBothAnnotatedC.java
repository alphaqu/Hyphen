package dev.quantumfusion.hyphen.scan.poly.extract.wrappedSuper;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1Super;
import dev.quantumfusion.hyphen.thr.UnknownTypeException;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
// TODO: fix
@FailTest(UnknownTypeException.class)
public class ExtractBothAnnotatedC {
	@DataSubclasses({C1.class, CoWrappedC1Super.class})
	public C1<@DataSubclasses({C1.class, C2.class}) C1<@DataSubclasses({Float.class, Integer.class}) Number>> data;

	public ExtractBothAnnotatedC(C1<C1<Number>> data) {
		this.data = data;
	}
}
