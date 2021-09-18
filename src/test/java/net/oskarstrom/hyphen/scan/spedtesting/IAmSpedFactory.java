package net.oskarstrom.hyphen.scan.spedtesting;

import net.oskarstrom.hyphen.TestUtil;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

public class IAmSpedFactory {

	@TestFactory
	public Stream<DynamicNode> spedTest() {
		return TestUtil.test(LifeStackedTest.class);
	}
}
