package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;
import net.oskarstrom.hyphen.scan.poly.classes.WrappedC1;

public class C1OfC1 {
	@Serialize
	@SerSubclasses({C1.class, WrappedC1.class})
	public C1<C1<Integer>> data;

	public C1OfC1(C1<C1<Integer>> data) {
		this.data = data;
	}
}
