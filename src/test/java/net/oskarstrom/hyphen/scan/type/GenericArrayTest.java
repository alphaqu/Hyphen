package net.oskarstrom.hyphen.scan.type;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.thr.IllegalClassException;

public class GenericArrayTest {
	@Serialize
	public Data<Integer> integerData;

	public GenericArrayTest(Data<Integer> integerData) {
		this.integerData = integerData;
	}

	@FailTest(IllegalClassException.class)
	public static class Data<K> {
		@Serialize
		public K[] array;

		public Data(K[] array) {
			this.array = array;
		}
	}
}
