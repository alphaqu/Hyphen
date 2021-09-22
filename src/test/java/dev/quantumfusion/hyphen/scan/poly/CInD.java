package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.D1;
import dev.quantumfusion.hyphen.scan.poly.classes.D2;


public class CInD {
	@Serialize
	@SerSubclasses({D1.class, D2.class})
	public D1<@SerSubclasses({C1.class, C2.class}) C1<Integer>> data;

	public CInD(D1<C1<Integer>> data) {
		this.data = data;
	}
}
