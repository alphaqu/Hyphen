package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.IReversedPair;
import net.oskarstrom.hyphen.scan.poly.classes.Pair;
import net.oskarstrom.hyphen.scan.poly.classes.ReversePair;

public class IReversePairAndReverseTest {
	@Serialize
	@SerSubclasses({Pair.class, ReversePair.class})
	public IReversedPair<Integer, Float> data;


	public IReversePairAndReverseTest(IReversedPair<Integer, Float> data) {
		this.data = data;
	}
}
