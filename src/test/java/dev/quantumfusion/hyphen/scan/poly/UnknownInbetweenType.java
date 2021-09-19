package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.C3Def;

public class UnknownInbetweenType {
	@Serialize
	@SerSubclasses({C1.class, C3Def.class})
	public C1<Integer> integer;


	public UnknownInbetweenType(C1<Integer> integer) {
		this.integer = integer;
	}
}
