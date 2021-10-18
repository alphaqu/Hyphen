package dev.quantumfusion.hyphen.scan.poly.extract.wrappedExtends;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1Extends;
import dev.quantumfusion.hyphen.util.TestThis;

@FailTest(/*NotYetImplementedException.class*/)
@Data
@TestThis
public class ExtractWildcardC {
	@DataSubclasses({C1.class, CoWrappedC1Extends.class})
	public C1<? extends C1<Integer>> data;

	public ExtractWildcardC(C1<? extends C1<Integer>> data) {
		this.data = data;
	}
}
