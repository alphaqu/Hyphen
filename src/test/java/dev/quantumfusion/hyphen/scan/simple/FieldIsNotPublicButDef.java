package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;


@TestThis
@FailTest // FIXME
public class FieldIsNotPublicButDef {
	@Data
	public int thing;

	private int ignore;


	public FieldIsNotPublicButDef(int thing) {
		this.thing = thing;
	}
}
