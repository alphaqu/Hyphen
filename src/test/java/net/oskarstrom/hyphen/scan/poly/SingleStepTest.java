package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;

public class SingleStepTest {
	@Serialize
	@SerSubclasses({C1.class, C2.class})
	public C1<Integer> integer;


	public SingleStepTest(C1<Integer> integer) {
		this.integer = integer;
	}
}
