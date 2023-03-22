package dev.notalpha.hyphen.test.poly.wildcards;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.pair.IPair;
import dev.notalpha.hyphen.test.poly.classes.pair.Pair;
import dev.notalpha.hyphen.test.poly.classes.pair.ReversePair;
import dev.notalpha.hyphen.util.TestThis;

@TestThis
public class SimpleDoubleExtends {
    public C1<@DataSubclasses({Pair.class, ReversePair.class})
			IPair<Float, Integer>> data;

    public SimpleDoubleExtends(C1<IPair<Float, Integer>> data) {
        this.data = data;
    }
/*
	@FailTest(IllegalClassException.class)
	static final class Test<T extends IPair<Integer, Float> & IReversedPair<Float, Integer>>{
		@Serialize
		@DataSubclasses({C1.class, C2.class})
		public C1<T> c;

		@Serialize
		public T t;

		public Test(C1<T> c, T t) {
			this.c = c;
			this.t = t;
		}
	}*/
}
