package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;

public class PolyArray {
	@Serialize
	public @SerSubclasses({Integer.class, Float.class}) Number[] numbers;

	public PolyArray(Number @SerSubclasses({Integer.class, Float.class}) [] numbers) {
		this.numbers = numbers;
	}
}
