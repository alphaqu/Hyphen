package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.Pair;
import dev.quantumfusion.hyphen.scan.poly.classes.ReversePair;

public class PairAndReverseTest {
	@Serialize
	@SerSubclasses({Pair.class, ReversePair.class})
	public Pair<Integer, Float> data;


	public PairAndReverseTest(Pair<Integer, Float> data) {
		this.data = data;
	}
}
