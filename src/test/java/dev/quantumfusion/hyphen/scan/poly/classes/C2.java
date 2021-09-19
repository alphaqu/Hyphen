package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.thr.IllegalClassException;

@FailTest(IllegalClassException.class)
public class C2<B> extends C1<B> {
	@Serialize
	public B b;

	public C2(B b, B b1) {
		super(b);
		this.b = b1;
	}
}
