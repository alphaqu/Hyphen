package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
@FailTest // FIXME: what is this test for?
public class FieldIsNotPublicButDef {
	int thing;

	public FieldIsNotPublicButDef(int thing) {
		this.thing = thing;
	}
}
