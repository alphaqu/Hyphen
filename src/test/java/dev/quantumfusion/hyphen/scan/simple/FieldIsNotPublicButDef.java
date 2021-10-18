package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class FieldIsNotPublicButDef {
	int thing;

	public FieldIsNotPublicButDef(int thing) {
		this.thing = thing;
	}
}
