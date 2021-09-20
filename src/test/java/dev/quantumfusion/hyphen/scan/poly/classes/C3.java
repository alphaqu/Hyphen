package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.annotation.Serialize;

public class C3<C, D> extends C2<C> {
	@Serialize
	public D d;

	public C3(C c, C b1, D d) {
		super(c, b1);
		this.d = d;
	}
}
