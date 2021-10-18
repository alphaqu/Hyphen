package dev.quantumfusion.hyphen.scan.poly.wildcards;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class SimpleSuper {
	@DataSubclasses({C1.class, C2.class})
	public C1<@DataSubclasses({Integer.class, Float.class})
			? super Number> data;

	public SimpleSuper(C1<? super Number> data) {
		this.data = data;
	}
}
