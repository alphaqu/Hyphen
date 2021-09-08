package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.Serialize;
import org.junit.jupiter.api.Test;

public class ObjectTest {

	@Test
	public void mainTest() {
		SerializerFactory debug = SerializerFactory.createDebug();
		debug.build(BasicScanTest.class);
	}

	public static class BasicScanTest {
		@Serialize
		public int integer;

		@Serialize
		public TestingObjectScan object;

		@Serialize
		public TestingObjectScan testingDeduplication;

		@Serialize
		public TestingInhiritedField inhiritedField;

		public BasicScanTest(int integer, TestingObjectScan object, TestingObjectScan testingDeduplication, TestingInhiritedField inhiritedField) {
			this.integer = integer;
			this.object = object;
			this.testingDeduplication = testingDeduplication;
			this.inhiritedField = inhiritedField;
		}
	}


	public static class TestingObjectScan {
		@Serialize
		public int something;

		public TestingObjectScan(int something) {
			this.something = something;
		}
	}

	public static class TestingInhiritedField extends ImYoSuper {
		@Serialize
		public int something;


		public TestingInhiritedField(int SUPERFIELD, int something) {
			super(SUPERFIELD);
			this.something = something;
		}
	}

	public static class ImYoSuper {
		@Serialize
		public int SUPERFIELD;

		public ImYoSuper(int SUPERFIELD) {
			this.SUPERFIELD = SUPERFIELD;
		}
	}
}
