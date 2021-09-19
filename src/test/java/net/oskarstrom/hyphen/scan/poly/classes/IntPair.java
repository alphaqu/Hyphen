package net.oskarstrom.hyphen.scan.poly.classes;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.thr.IllegalClassException;

@FailTest(IllegalClassException.class)
public class IntPair<B> extends Pair<Integer, B> {
	public IntPair(Integer integer, B b) {
		super(integer, b);
	}

	// FIXME: tracking issue 10
	public IntPair(Object object, B b){
		this((Integer) object, b);
	}
}