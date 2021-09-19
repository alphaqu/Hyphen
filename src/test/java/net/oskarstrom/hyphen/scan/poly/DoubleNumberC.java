package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;
import net.oskarstrom.hyphen.scan.poly.classes.C2;

public class DoubleNumberC {
	@Serialize
	@SerSubclasses({C1.class, C2.class})
	public C1< @SerSubclasses({C1.class, C2.class}) C1<@SerSubclasses({Integer.class, Float.class}) Number>> data;

	public DoubleNumberC(C1<C1<Number>> data) {
		this.data = data;
	}
}
