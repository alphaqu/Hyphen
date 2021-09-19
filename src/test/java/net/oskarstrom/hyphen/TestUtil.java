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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtil {

	public static Iterator<DynamicNode> testPackage(String path) {
		String packageName = "net.oskarstrom.hyphen." + path;
		InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));

		try {
			List<DynamicNode> tests = new ArrayList<>();
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if (line.endsWith(".class")) {
					Class<?> aClass = getClass(line, packageName);
					assert aClass != null;
					FailTest failTest = aClass.getDeclaredAnnotation(FailTest.class);
					Executable executable;
					if (failTest != null) {
						executable = () -> {
							Class<? extends Throwable> value = failTest.value();
							try {
								SerializerFactory.createDebug(aClass).build();
								fail();
							} catch (Throwable throwable) {
								if (throwable.getClass().equals(value)) assertTrue(true, throwable.getMessage());
								else fail(throwable);
							}
						};
					} else {
						executable = () -> SerializerFactory.createDebug(aClass).build();
					}

					tests.add(DynamicTest.dynamicTest(aClass.getSimpleName(), URI.create("class:" + aClass.getName()), executable));
				}
			}

			br.close();
			return tests.iterator();
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new RuntimeException();
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
