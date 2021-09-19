package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.thr.IllegalClassException;
import dev.quantumfusion.hyphen.thr.UnknownTypeException;

@FailTest(UnknownTypeException.class)
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
