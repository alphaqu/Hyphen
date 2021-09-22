package dev.quantumfusion.hyphen.scan.poly.extract.wrappedSuper;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1Super;

public class ExtractC {
	@Serialize
	@SerSubclasses({C1.class, CoWrappedC1Super.class})
	public C1<C1<Integer>> data;

	public ExtractC(C1<C1<Integer>> data) {
		this.data = data;
	}
}
