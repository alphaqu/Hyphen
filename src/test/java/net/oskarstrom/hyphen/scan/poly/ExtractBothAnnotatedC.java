package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;
import net.oskarstrom.hyphen.scan.poly.classes.CoWrappedC1;

public class ExtractBothAnnotatedC {
	@Serialize
	@SerSubclasses({C1.class, CoWrappedC1.class})
	public C1<@SerSubclasses({C1.class, CoWrappedC1.class}) C1<@SerSubclasses({Float.class, Integer.class}) Number>> data;

	public ExtractBothAnnotatedC(C1<C1<Number>> data) {
		this.data = data;
	}
}
