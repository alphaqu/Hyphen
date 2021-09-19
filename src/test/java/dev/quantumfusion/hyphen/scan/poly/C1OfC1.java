package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.WrappedC1;

public class C1OfC1 {
	@Serialize
	@SerSubclasses({C1.class, WrappedC1.class})
	public C1<C1<Integer>> data;

	public C1OfC1(C1<C1<Integer>> data) {
		this.data = data;
	}
}
