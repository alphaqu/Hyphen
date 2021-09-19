package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

public class MultiDimensionalArray {

	@Serialize
	public ObjectTest[][] whatAmIDoingInMyLife;

	public MultiDimensionalArray(ObjectTest[][] whatAmIDoingInMyLife) {
		this.whatAmIDoingInMyLife = whatAmIDoingInMyLife;
	}
}
