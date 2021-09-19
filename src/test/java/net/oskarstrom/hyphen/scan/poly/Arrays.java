package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;

public class Arrays {
	@Serialize
	@SerSubclasses({Integer[].class, Float[].class})
	public Number[] numbers;

	public Arrays(Number[] numbers) {
		this.numbers = numbers;
	}
}