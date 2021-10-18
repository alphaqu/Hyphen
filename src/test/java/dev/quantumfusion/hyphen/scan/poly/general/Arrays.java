package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
// FIXME
@FailTest(msg = "Integer[] does not inherit Number[]")
public class Arrays {
	public Number @DataSubclasses({Integer[].class, Float[].class})[] numbers;

	public Arrays(Number[] numbers) {
		this.numbers = numbers;
	}
}