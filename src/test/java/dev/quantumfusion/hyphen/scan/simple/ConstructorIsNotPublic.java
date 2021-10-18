package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

@FailTest(/*AccessException.class*/)
@Data
@TestThis
public class ConstructorIsNotPublic {
	public int prim;

	ConstructorIsNotPublic(int prim) {
		this.prim = prim;
	}
}
