package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;
import net.oskarstrom.hyphen.scan.poly.classes.RecursiveC;

public class Recursive {
	@Serialize
	@SerSubclasses({C1.class, RecursiveC.class})
	public C1<String> data;

	public Recursive(C1<String> data) {
		this.data = data;
	}
}
