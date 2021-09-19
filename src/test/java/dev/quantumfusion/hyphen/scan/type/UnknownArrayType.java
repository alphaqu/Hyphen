package dev.quantumfusion.hyphen.scan.type;

<<<<<<< HEAD:src/test/java/dev/quantumfusion/hyphen/scan/type/UnknownArrayType.java
import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.thr.ClassScanException;
import dev.quantumfusion.hyphen.thr.IllegalClassException;
=======
import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.thr.IllegalClassException;
import net.oskarstrom.hyphen.thr.NotYetImplementedException;
>>>>>>> origin/union-types:src/test/java/net/oskarstrom/hyphen/scan/type/UnknownArrayType.java

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
