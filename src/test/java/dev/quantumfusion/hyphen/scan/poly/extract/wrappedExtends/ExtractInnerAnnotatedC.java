package dev.quantumfusion.hyphen.scan.poly.extract.wrappedExtends;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1Extends;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class ExtractInnerAnnotatedC {
	@DataSubclasses({C1.class, CoWrappedC1Extends.class})
	public C1<C1<@DataSubclasses({Float.class, Integer.class}) Number>> data;

	public ExtractInnerAnnotatedC(C1<C1<Number>> data) {
		this.data = data;
	}
}
