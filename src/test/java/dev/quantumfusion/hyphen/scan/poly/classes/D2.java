package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.annotations.Data;

public class D2<B> extends D1<B> {
	@Data
	public B b;

	public D2(B b, B b1) {
		super(b);
		this.b = b1;
	}
}
