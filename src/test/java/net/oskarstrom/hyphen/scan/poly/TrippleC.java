package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;
import net.oskarstrom.hyphen.scan.poly.classes.C2;

// produces the wrong type info
@FailTest
public class TrippleC {
	@Serialize
	@SerSubclasses({C1.class, C2.class})
	public C1< @SerSubclasses({C1.class, C2.class}) C1< @SerSubclasses({C1.class, C2.class}) C1<Integer>>> data;

	public TrippleC(C1<C1<C1<Integer>>> data) {
		this.data = data;
	}
}
