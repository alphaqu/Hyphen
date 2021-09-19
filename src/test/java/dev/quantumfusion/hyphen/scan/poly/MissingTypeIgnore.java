package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C3Ignore;

public class MissingTypeIgnore {
	@Serialize
	@SerSubclasses({C1.class, C3Ignore.class})
	public C1<Integer> integer;


	public MissingTypeIgnore(C1<Integer> integer) {
		this.integer = integer;
	}
}
