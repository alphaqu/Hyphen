package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

public class FieldIsNotPublicButDef {

	@Serialize
	int thing;

	public FieldIsNotPublicButDef(int thing) {
		this.thing = thing;
	}
}
