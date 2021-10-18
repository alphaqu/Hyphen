package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class PolyArray {
	public @DataSubclasses({Integer.class, Float.class}) Number[] numbers;

	public PolyArray(Number[] numbers) {
		this.numbers = numbers;
	}
}
