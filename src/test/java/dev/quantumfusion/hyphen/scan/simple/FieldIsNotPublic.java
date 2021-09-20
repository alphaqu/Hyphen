package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.thr.AccessException;

@FailTest(AccessException.class)
public class FieldIsNotPublic {
	@Serialize
	Object thing;

	public FieldIsNotPublic(Object thing) {
		this.thing = thing;
	}

}
