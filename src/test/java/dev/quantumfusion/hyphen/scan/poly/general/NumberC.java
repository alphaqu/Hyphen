package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class NumberC {
	@DataSubclasses({C1.class, C2.class})
	public C1<@DataSubclasses({Integer.class, Float.class}) Number> data;

	public NumberC(C1<Number> data) {
		this.data = data;
	}
}
