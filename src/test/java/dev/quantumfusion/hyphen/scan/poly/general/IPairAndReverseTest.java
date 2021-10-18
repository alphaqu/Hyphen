package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.IPair;
import dev.quantumfusion.hyphen.scan.poly.classes.Pair;
import dev.quantumfusion.hyphen.scan.poly.classes.ReversePair;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class IPairAndReverseTest {
	@DataSubclasses({Pair.class, ReversePair.class})
	public IPair<Integer, Float> data;


	public IPairAndReverseTest(IPair<Integer, Float> data) {
		this.data = data;
	}
}
