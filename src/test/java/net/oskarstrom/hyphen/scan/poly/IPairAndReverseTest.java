package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.IPair;
import net.oskarstrom.hyphen.scan.poly.classes.Pair;
import net.oskarstrom.hyphen.scan.poly.classes.ReversePair;

public class IPairAndReverseTest {
	@Serialize
	@SerSubclasses({Pair.class, ReversePair.class})
	public IPair<Integer, Float> data;


	public IPairAndReverseTest(IPair<Integer, Float> data) {
		this.data = data;
	}
}
