package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class RecursiveC<T> extends C1<T> {
	@Data
	@DataSubclasses({C1.class, RecursiveC.class})
	public C1<T> foo;

	public RecursiveC(T t, C1<T> foo) {
		super(t);
		this.foo = foo;
	}



	@Override
	public String toString() {
		return "RecursiveC{" +
				"a=" + this.a +
				", foo=" + this.foo +
				'}';
	}
}

