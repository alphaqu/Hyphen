package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;
import net.oskarstrom.hyphen.scan.poly.classes.CoWrappedC1;
import net.oskarstrom.hyphen.thr.NotYetImplementedException;

@FailTest(NotYetImplementedException.class)
public class ExtractWildcardC {
	@Serialize
	@SerSubclasses({C1.class, CoWrappedC1.class})
	public C1<? extends C1<Integer>> data;

	public ExtractWildcardC(C1<? extends C1<Integer>> data) {
		this.data = data;
	}
}
