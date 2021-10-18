package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
@FailTest(/*AccessException.class*/)
public class FieldIsNotPublic {
	Object thing;

	public FieldIsNotPublic(Object thing) {
		this.thing = thing;
	}

}
