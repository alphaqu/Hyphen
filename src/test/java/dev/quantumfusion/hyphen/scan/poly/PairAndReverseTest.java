package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.Pair;
import net.oskarstrom.hyphen.scan.poly.classes.ReversePair;

public class PairAndReverseTest {
	@Serialize
	@SerSubclasses({Pair.class, ReversePair.class})
	public Pair<Integer, Float> data;


	public PairAndReverseTest( Pair<Integer, Float> data) {
		this.data = data;
	}
}
