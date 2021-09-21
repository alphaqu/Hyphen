package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.thr.exception.AccessException;

@FailTest(AccessException.class)
public class ConstructorIsNotPublic {
	@Serialize
	public int prim;

	ConstructorIsNotPublic(int prim) {
		this.prim = prim;
	}
}
