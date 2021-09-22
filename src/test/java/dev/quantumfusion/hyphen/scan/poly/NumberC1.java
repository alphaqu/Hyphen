package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;


public class NumberC1 {
	@Serialize
	public C1<@SerSubclasses({Integer.class, Float.class}) Object> data;

	public NumberC1(C1<Object> data) {
		this.data = data;
	}
}
