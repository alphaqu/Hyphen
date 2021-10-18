package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.SerializerFactory;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;


public class TestUtil {
	public static Set<Class<?>> findAllClassesUsingClassLoader(String packageName) {
		InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		return reader.lines()
				.filter(line -> line.endsWith(".class"))
				.map(line -> getClass(line, packageName))
				.collect(Collectors.toSet());
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

	@Test
	void test() {
		try {
			testAll("dev.quantumfusion.hyphen.scan.poly.classes");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void testAll(String packageName) throws InvocationTargetException, IllegalAccessException {
		final Set<Class<?>> allClassesUsingClassLoader = findAllClassesUsingClassLoader(packageName);
		for (Class<?> aClass : allClassesUsingClassLoader) {
			test(aClass);
		}
	}


	public static <O> void test(Class<O> clazz) throws InvocationTargetException, IllegalAccessException {
		if (clazz.getDeclaredAnnotation(TestThis.class) != null) {
			System.out.println("Testing " + clazz.getSimpleName());
			// generation
			var factory = SerializerFactory.create(ByteBufferIO.class, clazz);
			var serializer = factory.build();

			var expectedScan = clazz.getDeclaredAnnotation(ExpectedResult.class);
			if (expectedScan != null) {
				// TODO add fingerprint methods that check if scan was correct
				var expected = expectedScan.value();
			}

			// create data
			Stream<O> datas;
			try {

				datas = (Stream<O>) clazz.getDeclaredMethod("generate" + clazz.getSimpleName(), Stream.class).invoke(null, Stream.of(432,24343,3142,314));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return;
			}

			datas.forEach(data -> {
				// measure
				var measuredSize = serializer.measure(data);
				var io = ByteBufferIO.create(measuredSize * 2);

				// put
				serializer.put(io, data);

				// measure check
				final int actualSize = io.pos();
				if (actualSize > measuredSize)
					fail("Actual size is bigger than measured size. " + actualSize + " > " + measuredSize);
				else if (measuredSize > actualSize)
					fail("Measured size is bigger than actual size. " + measuredSize + " > " + actualSize);

				// rewind to 0
				io.rewind();

				// get
				var dataOut = serializer.get(io);

				// result check
				if (!data.equals(dataOut))
					fail("Objects do not match\n" + data + "\n != \n" + dataOut);
			});
		} else {
			System.out.println("Skipped " + clazz.getSimpleName());
		}
	}
}
