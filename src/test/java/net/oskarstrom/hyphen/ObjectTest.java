package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.Serialize;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

public class ObjectTest {

	@Test
	public void mainTest() {
		SerializerFactory.create().build(SpedTest3.class);
	}

	public static class SpedTest3 {
		@Serialize
		SpedTest2 testObject;
		@Serialize
		SpedTest2 testObjecft;
		@Serialize
		SpedTest2 testOfbject;
		@Serialize
		SpedTest2 tfestObject;
		@Serialize
		SpedTest2 tefstObject;
		@Serialize
		SpedTest2 tgestObject;
		@Serialize
		SpedTest2 tesgtObject;
		@Serialize
		SpedTest2 testObgject;
		@Serialize
		SpedTest2 testObjegct;
		@Serialize
		SpedTest2 testObjecgt;
		@Serialize
		SpedTest2 testObjecht;
		@Serialize
		SpedTest2 testObjehct;
		@Serialize
		SpedTest2 testObhject;
		@Serialize
		SpedTest2 testhObject;
		@Serialize
		SpedTest2 tehstObject;
		@Serialize
		SpedTest2 thestObject;
		@Serialize
		SpedTest2 tejstObject;
		@Serialize
		SpedTest2 testOjbject;
		@Serialize
		SpedTest2 tejstObjehct;
	}

	public static class SpedTest2 {
		@Serialize
		SpedTest testObject;
		@Serialize
		SpedTest testObjecft;
		@Serialize
		SpedTest testOfbject;
		@Serialize
		SpedTest tfestObject;
		@Serialize
		SpedTest tefstObject;
		@Serialize
		SpedTest tgestObject;
		@Serialize
		SpedTest tesgtObject;
		@Serialize
		SpedTest testObgject;
		@Serialize
		SpedTest testObjegct;
		@Serialize
		SpedTest testObjecgt;
		@Serialize
		SpedTest testObjecht;
		@Serialize
		SpedTest testObjehct;
		@Serialize
		SpedTest testObhject;
		@Serialize
		SpedTest testhObject;
		@Serialize
		SpedTest tehstObject;
		@Serialize
		SpedTest thestObject;
		@Serialize
		SpedTest tejstObject;
		@Serialize
		SpedTest testOjbject;
		@Serialize
		SpedTest tejstObjehct;
	}

	public static class SpedTest {
		@Serialize
		TestObject testObject;
		@Serialize
		TestObject testObjecft;
		@Serialize
		TestObject testOfbject;
		@Serialize
		int integer2;
		@Serialize
		int integer;
	}


	public static class TestObject {
		@Serialize
		public TestObjectWithType<HashMap<Integer, List<TestObjectWithType<String>>>> thingie4;

		@Serialize
		public TestObjectWithType<HashMap<List<TestObjectWithType<String>>, Integer>> thingie3;
	}

	public static class TestObjectWithType<K> {
		@Serialize
		public K typeThing;
	}

	public static class TestObjectWithTypePathTest<K> {
		@Serialize
		public K typeThing;
		@Serialize
		public boolean bool;
	}
}
