package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1;
import dev.quantumfusion.hyphen.thr.NotYetImplementedException;

@FailTest(NotYetImplementedException.class)
public class ExtractWildcardC {
	@Serialize
	@SerSubclasses({C1.class, CoWrappedC1.class})
	public C1<? extends C1<Integer>> data;

	public ExtractWildcardC(C1<? extends C1<Integer>> data) {
		this.data = data;
	}
}
