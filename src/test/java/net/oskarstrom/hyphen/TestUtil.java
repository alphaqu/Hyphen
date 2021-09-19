package net.oskarstrom.hyphen;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class TestUtil {

	public static Iterator<DynamicNode> testPackage(String path) {
		String packageName = "net.oskarstrom.hyphen." + path;
		InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));

		try {
			List<DynamicNode> tests = new ArrayList<>();
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if (line.endsWith(".class")) tests.add(test(getClass(line, packageName)));
			}

			br.close();
			return tests.iterator();
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new RuntimeException();
	}
	public static DynamicNode test(Class<?> clazz) {
		assert clazz != null;
		FailTest failTest = clazz.getDeclaredAnnotation(FailTest.class);
		Executable executable;
		if (failTest != null) {
			executable = () -> {
				Class<? extends Throwable> value = failTest.value();
				try {
					SerializerFactory.createDebug(clazz).build();
				} catch (Throwable throwable) {
					if (throwable.getClass().equals(value)) {
						System.err.println("Got expected error: ");
						throwable.printStackTrace();
						return;
					}
					fail("Expected a different exception: " + value.getSimpleName(), throwable);
				}

				if(value == Throwable.class){
					fail("Forcefully failed test");
				} else {
					fail("Expected test to fail");
				}
			};
		} else {
			executable = () -> SerializerFactory.createDebug(clazz).build();
		}

		return DynamicTest.dynamicTest(clazz.getSimpleName(), URI.create("class:" + clazz.getName()), executable);
	}


	private static Class<?> getClass(String className, String packageName) {
		try {
			return Class.forName(packageName + "."
					+ className.substring(0, className.lastIndexOf('.')));
		} catch (ClassNotFoundException e) {
			// handle the exception
		}
		return null;
	}
}
