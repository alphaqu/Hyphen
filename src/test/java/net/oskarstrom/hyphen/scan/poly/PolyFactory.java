package net.oskarstrom.hyphen.scan.poly;

import net.oskarstrom.hyphen.TestUtil;
import net.oskarstrom.hyphen.scan.simple.ExtendTest;
import net.oskarstrom.hyphen.scan.simple.ObjectTest;
import net.oskarstrom.hyphen.scan.simple.PrimitiveTest;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

public class PolyFactory {

	@TestFactory
	public Stream<DynamicNode> simpleTest() {
		return TestUtil.test(SingleStepTest.class);
	}
}
