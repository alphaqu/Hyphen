package dev.quantumfusion.hyphen.scan.poly.wildcards;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;

public class SimpleSuper {
	@Serialize
	@SerSubclasses({C1.class, C2.class})
	public C1<@SerSubclasses({Integer.class, Float.class})
			? super Number> data;

	public SimpleSuper(C1<? super Number> data) {
		this.data = data;
	}
}
