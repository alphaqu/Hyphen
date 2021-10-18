package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class DoubleC {
	@DataSubclasses({C1.class, C2.class})
	public C1<@DataSubclasses({C1.class, C2.class}) C1<Integer>> data;

	public DoubleC(C1<C1<Integer>> data) {
		this.data = data;
	}
}
