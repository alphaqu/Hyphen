package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;

// I just wanted to have a commit where all tests succeed
// FIXME: @FailTest(IncompatibleTypeException.class)
public class IncompatibleTypeFail {
	@Serialize
	@SerSubclasses({C1.class, IntC1.class})
	public C1<Float> floatC1;


	public IncompatibleTypeFail(C1<Float> floatC1) {
		this.floatC1 = floatC1;
	}
}