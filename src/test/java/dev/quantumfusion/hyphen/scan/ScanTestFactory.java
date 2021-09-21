package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.TestUtil;
import dev.quantumfusion.hyphen.scan.poly.IReversePairAndReverseTest;
import dev.quantumfusion.hyphen.scan.poly.TestIssue10;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestFactory;

import java.util.Collections;
import java.util.Iterator;

public class ScanTestFactory {

	@TestFactory
	Iterator<DynamicNode> bareTest() {
		return Collections.singletonList(TestUtil.test(IReversePairAndReverseTest.class)).iterator();
	}

	@Nested
	public class SimpleSparkPlug {
		@TestFactory
		Iterator<DynamicNode> simpleTests() {
			return TestUtil.testPackage("scan.simple");
		}
	}

	@Nested
	public class TypeSparkPlug {
		@TestFactory
		Iterator<DynamicNode> typeTests() {
			return TestUtil.testPackage("scan.type");
		}
	}

	@Nested
	public class PolySparkPlug {
		@TestFactory
		Iterator<DynamicNode> polyTests() {
			return TestUtil.testPackage("scan.poly");
		}
	}
}
