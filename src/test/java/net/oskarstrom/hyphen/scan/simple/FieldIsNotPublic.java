package net.oskarstrom.hyphen.scan.simple;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.thr.AccessException;

@FailTest(AccessException.class)
public class FieldIsNotPublic {
	@Serialize
	Object thing;

	public FieldIsNotPublic(Object thing) {
		this.thing = thing;
	}

}
