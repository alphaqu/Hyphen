package net.oskarstrom.hyphen.scan.simple;

import net.oskarstrom.hyphen.TestUtil;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

public class SimpleFactory {

	@TestFactory
	public Stream<DynamicNode> simpleTest() {
		return TestUtil.test(ExtendTest.class, ObjectTest.class, PrimitiveTest.class, ObjectArrayTest.class, ArrayTest.class, MultiDimensionalArray.class);
	}
}
