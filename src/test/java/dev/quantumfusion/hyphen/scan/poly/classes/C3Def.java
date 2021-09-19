package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.thr.IllegalClassException;

@FailTest(IllegalClassException.class)
public class C3Def<E> extends C3<E,String> {
	@Serialize
	public E e;

	public C3Def(E e, E b1, String s, E e1) {
		super(e, b1, s);
		this.e = e1;
	}

	// FIXME: Tracking issue #10
	public C3Def(E e, E b1, Object s, E e1) {
		this(e, b1, (String) s, e1);
	}
}
