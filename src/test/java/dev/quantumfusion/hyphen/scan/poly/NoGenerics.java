package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;

public class NoGenerics {
	@Serialize
	@SerSubclasses({Integer.class, Float.class})
	public Number number;


	public NoGenerics(Number number) {
		this.number = number;
	}
}
