package net.oskarstrom.hyphen;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Stream;

public class TestUtil {

	public static Stream<DynamicNode> test(Class<?>... classes) {
		return Arrays.stream(classes).map(clz -> {
					String desc = "";
					Description declaredAnnotation = clz.getDeclaredAnnotation(Description.class);
					if (declaredAnnotation != null) {
						desc = " - " + declaredAnnotation.value();
					}
					return DynamicTest.dynamicTest(clz.getSimpleName() + desc, URI.create("class:" + clz.getName()), () -> {
						System.out.println(clz.getName());
						SerializerFactory debug = SerializerFactory.createDebug();
						debug.build(clz);
					});
				}
		);
	}
}
