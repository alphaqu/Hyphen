package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.SerNull;
import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ObjectTest {

	@Test
	public void mainTest() {
		SerializerFactory debug = SerializerFactory.createDebug();
		debug.build(BasicScanTest.class);
	}

	public static class BasicScanTest {
		@Serialize
		@SerNull
		public int integer;

		@Serialize
		@SerNull
		public TestingObjectScan object;

		@Serialize
		public TestingObjectScan testingDeduplication;

		@SerSubclasses(Integer.class)
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

		public TestingInhiritedField(int something, int SUPERFIELD, ArrayList<@SerNull Integer> list) {
			super(SUPERFIELD, list);
			this.something = something;
		}
	}

	public static class ImYoSuper {
		@Serialize
		public int SUPERFIELD;
		@Serialize
		@SerNull
		public ArrayList<@SerNull Integer> list;

		public ImYoSuper(int SUPERFIELD, ArrayList<@SerNull Integer> list) {
			this.SUPERFIELD = SUPERFIELD;
			this.list = list;
		}
	}
}
