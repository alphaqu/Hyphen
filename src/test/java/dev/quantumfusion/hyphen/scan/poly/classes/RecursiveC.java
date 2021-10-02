package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.Recursive;

public class RecursiveC<T> extends C1<T> {
	@Serialize
	@SerSubclasses({C1.class, RecursiveC.class})
	public C1<T> foo;

	@Serialize
	public Recursive uwu;

	public RecursiveC(T t, C1<T> foo, Recursive uwu) {
		super(t);
		this.foo = foo;
		this.uwu = uwu;
	}
}

