package net.oskarstrom.hyphen.scan.poly.classes;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.thr.IllegalClassException;

@FailTest(IllegalClassException.class)
public class RecursiveC<T> extends C1<T> {
	@Serialize
	@SerSubclasses({C1.class, RecursiveC.class})
	public C1<T> foo;

	public RecursiveC(T t, C1<T> foo) {
		super(t);
		this.foo = foo;
	}
}

