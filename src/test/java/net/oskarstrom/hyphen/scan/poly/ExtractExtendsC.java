package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;
import net.oskarstrom.hyphen.scan.poly.classes.C2;
import net.oskarstrom.hyphen.scan.poly.classes.CoWrappedC1;
import net.oskarstrom.hyphen.thr.NotYetImplementedException;

// Tracking issue #6
@FailTest(NotYetImplementedException.class)
public class ExtractExtendsC {
	@Serialize
	@SerSubclasses({C1.class, CoWrappedC1.class})
	public C1<C2<Integer>> data;

	public ExtractExtendsC(C1<C2<Integer>> data) {
		this.data = data;
	}
}
