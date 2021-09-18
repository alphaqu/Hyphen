package net.oskarstrom.hyphen.scan.type;

import net.oskarstrom.hyphen.TestUtil;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

public class TypeFactory {

	@TestFactory
	public Stream<DynamicNode> typeTest() {
		return TestUtil.test(InheritImplTest.class, SimpleTypeTest.class);
	}
}
