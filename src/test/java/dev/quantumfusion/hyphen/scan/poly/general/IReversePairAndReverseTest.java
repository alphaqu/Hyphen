package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.IReversedPair;
import dev.quantumfusion.hyphen.scan.poly.classes.Pair;
import dev.quantumfusion.hyphen.scan.poly.classes.ReversePair;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class IReversePairAndReverseTest {
	@DataSubclasses({Pair.class, ReversePair.class})
	public IReversedPair<Integer, Float> data;


	public IReversePairAndReverseTest(IReversedPair<Integer, Float> data) {
		this.data = data;
	}
}
