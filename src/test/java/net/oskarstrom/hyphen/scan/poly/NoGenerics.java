package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;

public class NoGenerics {
	@Serialize
	@SerSubclasses({Integer.class, Float.class})
	public Number number;


	public NoGenerics(Number number) {
		this.number = number;
	}
}
