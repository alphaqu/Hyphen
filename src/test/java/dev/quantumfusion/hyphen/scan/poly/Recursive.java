package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.RecursiveC;

public class Recursive {
	@Serialize
	@SerSubclasses({C1.class, RecursiveC.class})
	public C1<String> data;

	public Recursive(C1<String> data) {
		this.data = data;
	}
}
