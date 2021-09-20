package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.IPair;
import dev.quantumfusion.hyphen.scan.poly.classes.Pair;
import dev.quantumfusion.hyphen.scan.poly.classes.ReversePair;

public class IPairAndReverseTest {
	@Serialize
	@SerSubclasses({Pair.class, ReversePair.class})
	public IPair<Integer, Float> data;


	public IPairAndReverseTest(IPair<Integer, Float> data) {
		this.data = data;
	}
}
