package net.oskarstrom.hyphen.scan.type;

import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.thr.IllegalClassException;
import net.oskarstrom.hyphen.thr.NotYetImplementedException;

@FailTest(NotYetImplementedException.class)
public class UnknownArrayType {

	@Serialize
	public Data<?> integerData;

	public UnknownArrayType(Data<?> integerData) {
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
