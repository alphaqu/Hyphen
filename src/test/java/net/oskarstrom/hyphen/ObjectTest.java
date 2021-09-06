package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.Serialize;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

public class ObjectTest {

	@Test
	public void mainTest() {
		SerializerFactory debug = SerializerFactory.createDebug();
		debug.addSubclasses(ParameterizedSubclass.class, ParameterizedSubclass.Subclass2.class, ParameterizedSubclass.SubclassWithParameter.class);
		debug.build(SubclassTest.class);
	}

	public interface SubclassInterface {

		class Subclass implements SubclassInterface {
			@Serialize
			public int integer;
		}

		class Subclass2 implements SubclassInterface {
			@Serialize
			public boolean bool;
		}
	}

	public static class SubclassTest {
		@Serialize
		ParameterizedSubclass<String> subclassInterface;
	}

	public static class ArrayTest {
		@Serialize
		int[] array;
		@Serialize
		ParamArrayTest<String> paramArrayTest;
	}

	public static class ParamArrayTest<K> {
		@Serialize
		K[] array;

	}
	public static class SpedTest {
		@Serialize
		TestObject testObject;
		@Serialize
		int integer2;
		@Serialize
		int integer;
	}

	public static class TestObject {
		@Serialize
		public TestObjectWithType<HashMap<Integer, List<TestObjectWithType<String>>>> thingie4;

		@Serialize
		public List<?> thingie3;
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

	public abstract class ParameterizedSubclass<K> {
		@Serialize
		public K extraData;

		class SubclassWithParameter<K> extends ParameterizedSubclass<K> {

			@Serialize
			public int integer;

			@Serialize
			public K parameter;
		}

		class Subclass2 extends ParameterizedSubclass<Integer> {

			@Serialize
			public boolean bool;
		}

	}

}
