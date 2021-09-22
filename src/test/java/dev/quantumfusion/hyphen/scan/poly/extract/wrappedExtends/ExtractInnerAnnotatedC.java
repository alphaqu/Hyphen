package dev.quantumfusion.hyphen.scan.poly.extract.wrappedExtends;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1Extends;

public class ExtractInnerAnnotatedC {
	@Serialize
	@SerSubclasses({C1.class, CoWrappedC1Extends.class})
	public C1<C1<@SerSubclasses({Float.class, Integer.class}) Number>> data;

	public ExtractInnerAnnotatedC(C1<C1<Number>> data) {
		this.data = data;
	}
}
