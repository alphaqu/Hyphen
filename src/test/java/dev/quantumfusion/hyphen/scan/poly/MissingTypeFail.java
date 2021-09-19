package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;
import net.oskarstrom.hyphen.scan.poly.classes.C3;
import net.oskarstrom.hyphen.thr.MissingTypeInformationException;

@FailTest(MissingTypeInformationException.class)
public class MissingTypeFail {
	@Serialize
	@SerSubclasses({C1.class, C3.class})
	public C1<Integer> integer;


	public MissingTypeFail(C1<Integer> integer) {
		this.integer = integer;
	}
}
