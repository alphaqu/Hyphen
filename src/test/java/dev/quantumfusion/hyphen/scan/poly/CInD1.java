package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.D1;


public class CInD1 {
	@Serialize
	public D1<@SerSubclasses({C1.class, C2.class}) C1<Integer>> data;

	public CInD1(D1<C1<Integer>> data) {
		this.data = data;
	}
}
