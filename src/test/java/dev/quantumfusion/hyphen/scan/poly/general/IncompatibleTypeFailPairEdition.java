package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.pair.Pair;
import dev.quantumfusion.hyphen.scan.poly.classes.pair.SelfPair;
import dev.quantumfusion.hyphen.util.TestThis;

@FailTest(/*IncompatibleTypeException.class*/)
@Data
@TestThis
public class IncompatibleTypeFailPairEdition {
	@DataSubclasses({Pair.class, SelfPair.class})
	public Pair<Float, Integer> data;


	public IncompatibleTypeFailPairEdition(Pair<Float, Integer> data) {
		this.data = data;
	}
}
