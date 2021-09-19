package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;
import net.oskarstrom.hyphen.scan.poly.classes.C2;

//@FailTest(NotYetImplementedException.class)
public class Wildcard {
	@Serialize
	@SerSubclasses({C1.class, C2.class})
	public C1<@SerSubclasses({Integer.class, Float.class})
			? extends Number> data;

	public Wildcard(C1<? extends Number> data) {
		this.data = data;
	}
}
