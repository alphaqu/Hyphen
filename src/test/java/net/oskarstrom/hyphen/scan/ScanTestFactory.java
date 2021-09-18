package net.oskarstrom.hyphen.scan;

import net.oskarstrom.hyphen.scan.poly.PolyFactory;
import net.oskarstrom.hyphen.scan.simple.SimpleFactory;
import net.oskarstrom.hyphen.scan.type.TypeFactory;
import org.junit.jupiter.api.Nested;

public class ScanTestFactory {


	@Nested
	public class SimpleSparkPlug extends SimpleFactory {}

	@Nested
	public class TypeSparkPlug extends TypeFactory {}

	@Nested
	public class PolySparkPlug extends PolyFactory {}
}
