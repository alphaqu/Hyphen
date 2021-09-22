package dev.quantumfusion.hyphen.scan.poly.wildcards;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.IPair;
import dev.quantumfusion.hyphen.scan.poly.classes.Pair;
import dev.quantumfusion.hyphen.scan.poly.classes.ReversePair;

public class SimpleDoubleExtends {
	@Serialize
	public C1<@SerSubclasses({Pair.class, ReversePair.class})
			IPair<Float, Integer>> data;

	public SimpleDoubleExtends(C1<IPair<Float, Integer>> data) {
		this.data = data;
	}
/*
	@FailTest(IllegalClassException.class)
	static final class Test<T extends IPair<Integer, Float> & IReversedPair<Float, Integer>>{
		@Serialize
		@SerSubclasses({C1.class, C2.class})
		public C1<T> c;

		@Serialize
		public T t;

		public Test(C1<T> c, T t) {
			this.c = c;
			this.t = t;
		}
	}*/
}
