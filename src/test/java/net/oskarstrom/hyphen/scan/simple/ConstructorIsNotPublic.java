package net.oskarstrom.hyphen.scan.simple;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.thr.AccessException;

@FailTest(AccessException.class)
public class ConstructorIsNotPublic {
	@Serialize
	public int prim;

	ConstructorIsNotPublic(int prim) {
		this.prim = prim;
	}
}
