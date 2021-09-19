package net.oskarstrom.hyphen.scan.simple;

import net.oskarstrom.hyphen.annotation.Serialize;

public class FieldIsNotPublicButDef {

	@Serialize
	int thing;

	public FieldIsNotPublicButDef(int thing) {
		this.thing = thing;
	}
}
