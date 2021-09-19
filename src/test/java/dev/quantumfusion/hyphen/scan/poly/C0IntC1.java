package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C0;

public class C0IntC1 {
	@Serialize
	@SerSubclasses({C0.class, IntC1.class})
	public C0 data;

	public C0IntC1(C0 data) {
		this.data = data;
	}
}
