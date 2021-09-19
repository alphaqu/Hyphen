package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;
import net.oskarstrom.hyphen.scan.poly.classes.C2;
import net.oskarstrom.hyphen.scan.poly.classes.C3Def;

public class UnknownInbetweenType {
	@Serialize
	@SerSubclasses({C1.class, C3Def.class})
	public C1<Integer> integer;


	public UnknownInbetweenType(C1<Integer> integer) {
		this.integer = integer;
	}
}
