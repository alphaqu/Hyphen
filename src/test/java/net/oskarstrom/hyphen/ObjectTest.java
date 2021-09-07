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
		int integer;

		@Serialize
		TestingObjectScan object;

		@Serialize
		TestingObjectScan testingDeduplication;

		@Serialize
		TestingInhiritedField inhiritedField;
	}


	public static class TestingObjectScan {
		@Serialize
		public int something;

	}

	public static class TestingInhiritedField extends ImYoSuper {
		@Serialize
		public int something;


	}

	public static class ImYoSuper {
		@Serialize
		public int SUPERFIELD;
	}
}
