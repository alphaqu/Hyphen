package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class NumberC1 {
	public C1<@DataSubclasses({Integer.class, Float.class}) Object> data;

	public NumberC1(C1<Object> data) {
		this.data = data;
	}
}
