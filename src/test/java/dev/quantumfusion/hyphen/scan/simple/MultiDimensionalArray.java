package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

@Data
@TestThis
public class MultiDimensionalArray {
	public ObjectTest[][] whatAmIDoingInMyLife;

	public MultiDimensionalArray(ObjectTest[][] whatAmIDoingInMyLife) {
		this.whatAmIDoingInMyLife = whatAmIDoingInMyLife;
	}
}
