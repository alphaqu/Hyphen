package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;

public class Arrays {
	@Serialize
	@SerSubclasses({Integer[].class, Float[].class})
	public Number[] numbers;

	public Arrays(Number[] numbers) {
		this.numbers = numbers;
	}
}