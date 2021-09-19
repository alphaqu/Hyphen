package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.Pair;
import dev.quantumfusion.hyphen.scan.poly.classes.SelfPair;
import dev.quantumfusion.hyphen.thr.IncompatibleTypeException;

@FailTest(IncompatibleTypeException.class)
public class IncompatibleTypeFailPairEdition {
	@Serialize
	@SerSubclasses({Pair.class, SelfPair.class})
	public Pair<Float, Integer> data;


	public IncompatibleTypeFailPairEdition(Pair<Float, Integer> data) {
		this.data = data;
	}
}
