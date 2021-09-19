package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.Pair;
import net.oskarstrom.hyphen.scan.poly.classes.SelfPair;
import net.oskarstrom.hyphen.thr.IncompatibleTypeException;

@FailTest(IncompatibleTypeException.class)
public class IncompatibleTypeFailPairEdition {
	@Serialize
	@SerSubclasses({Pair.class, SelfPair.class})
	public Pair<Float, Integer> data;


	public IncompatibleTypeFailPairEdition(Pair<Float, Integer> data) {
		this.data = data;
	}
}
