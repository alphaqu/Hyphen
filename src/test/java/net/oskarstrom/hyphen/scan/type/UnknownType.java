package net.oskarstrom.hyphen.scan.type;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.thr.ClassScanException;
import net.oskarstrom.hyphen.thr.IllegalClassException;

@FailTest(ClassScanException.class)
public class UnknownType {

	@Serialize
	public Data<?> integerData;

	public UnknownType(Data<?> integerData) {
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
