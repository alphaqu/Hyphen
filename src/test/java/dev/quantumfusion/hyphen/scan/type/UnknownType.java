package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.thr.ClassScanException;
import dev.quantumfusion.hyphen.thr.IllegalClassException;

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
		public K array;

		public Data(K array) {
			this.array = array;
		}
	}
}
