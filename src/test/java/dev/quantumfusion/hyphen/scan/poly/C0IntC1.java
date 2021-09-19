package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C0;

public class C0IntC1 {
	@Serialize
	@SerSubclasses({C0.class, IntC1.class})
	public C0 data;

	public C0IntC1(C0 data) {
		this.data = data;
	}
}
