package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.IReversedPair;
import dev.quantumfusion.hyphen.scan.poly.classes.Pair;
import dev.quantumfusion.hyphen.scan.poly.classes.ReversePair;

public class IReversePairAndReverseTest {
	@Serialize
	@SerSubclasses({Pair.class, ReversePair.class})
	public IReversedPair<Integer, Float> data;


	public IReversePairAndReverseTest(IReversedPair<Integer, Float> data) {
		this.data = data;
	}
}
