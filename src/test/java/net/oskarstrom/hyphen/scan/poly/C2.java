package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.thr.IllegalClassException;

@FailTest(IllegalClassException.class)
public class C2<B> extends C1<B> {
	@Serialize
	public B b;

	public C2(B b, B b1) {
		super(b);
		this.b = b1;
	}
}
