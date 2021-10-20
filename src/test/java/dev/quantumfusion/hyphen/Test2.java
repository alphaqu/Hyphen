package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.scan.annotations.Data;

public class Test2<O> {
	@Data
	public final O o;

	public Test2(O o) {
		this.o = o;
	}
}
