package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class NoGenerics {
	@DataSubclasses({Integer.class, Float.class})
	public Number number;


	public NoGenerics(Number number) {
		this.number = number;
	}
}
