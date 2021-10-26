package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.SerializerFactory;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.poly.general.DoubleC1Pain;
import org.junit.jupiter.api.*;
import org.objectweb.asm.Opcodes;
import org.opentest4j.AssertionFailedError;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: cleanup
public class TestUtil {
	public static Stream<? extends Class<?>> findAllClassesUsingClassLoader(String packageName) {
		InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		return reader.lines()
				.filter(line -> line.endsWith(".class"))
				.map(line -> getClass(line, packageName));
	}

	public static Stream<? extends Class<?>> findTestClasses(String packageName) {
		return findAllClassesUsingClassLoader(packageName).filter(c -> c.isAnnotationPresent(TestThis.class));
	}

	private static Class<?> getClass(String className, String packageName) {
		String name = packageName + "." + className.substring(0, className.lastIndexOf('.'));
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Failed to load class " + name, e);
		}
	}

	@TestFactory
	DynamicNode testPolyExtractWrapped() {
		return testAll("dev.quantumfusion.hyphen.scan.poly.extract.wrapped");
	}

	@TestFactory
	DynamicNode testPolyExtractWrappedExtends() {
		return testAll("dev.quantumfusion.hyphen.scan.poly.extract.wrappedExtends");
	}

	@TestFactory
	DynamicNode testPolyExtractWrappedSuper() {
		return testAll("dev.quantumfusion.hyphen.scan.poly.extract.wrappedSuper");
	}

	@TestFactory
	DynamicNode testPolyGeneral() {
		return testAll("dev.quantumfusion.hyphen.scan.poly.general");
	}

	@TestFactory
	DynamicNode testPolyEnums() {
		return testAll("dev.quantumfusion.hyphen.scan.poly.enums");
	}

	@TestFactory
	DynamicNode testPolyWildcards() {
		return testAll("dev.quantumfusion.hyphen.scan.poly.wildcards");
	}

	@TestFactory
	DynamicNode testSimple() {
		return testAll("dev.quantumfusion.hyphen.scan.simple");
	}

	@TestFactory
	DynamicNode testSimpleMap() {
		return testAll("dev.quantumfusion.hyphen.scan.simple.map");
	}

	@TestFactory
	DynamicNode testSimpleArrays() {
		return testAll("dev.quantumfusion.hyphen.scan.simple.arrays");
	}

	public static DynamicNode testAll(String packageName) {
		return DynamicContainer.dynamicContainer(
				packageName,
				findTestClasses(packageName).map(TestUtil::test)
		);
	}

	@TestFactory
	public DynamicNode test5() {
		return test(DoubleC1Pain.class);
	}

	public static <O> DynamicNode test(Class<O> clazz) {
		try {
			// generation
			var factory = SerializerFactory.create(ByteBufferIO.class, clazz);
			factory.setOption(Options.SHORT_METHOD_NAMES, false);
			factory.setOption(Options.SHORT_VARIABLE_NAMES, false);
			var serializer = factory.build();

			var expectedScan = clazz.getDeclaredAnnotation(ExpectedResult.class);
			if (expectedScan != null) {
				// TODO add fingerprint methods that check if scan was correct
				var expected = expectedScan.value();
			}

			// create data
			Stream<? extends O> datas;
			try {
				//noinspection unchecked
				Method declaredMethod = clazz.getDeclaredMethod("generate" + clazz.getSimpleName());
				Assumptions.assumeTrue((declaredMethod.getModifiers() & Opcodes.ACC_STATIC) != 0, "generate is not static");
				datas = ((Supplier<? extends Stream<? extends O>>) declaredMethod.invoke(null)).get();
			} catch (NoSuchMethodException e) {

				Assumptions.assumeTrue(true, "missing generate method");
				return DynamicTest.dynamicTest(clazz.getSimpleName(), () -> {
				});
			}

			Assertions.assertFalse(clazz.isAnnotationPresent(FailTest.class), "Expected test to fail");
			return DynamicContainer.dynamicContainer(clazz.getSimpleName(),
					datas.limit(10000).map(data -> {
						String displayName;
						try {
							displayName = data.toString();
						} catch (Throwable t) {
							return DynamicTest.dynamicTest("<unknown>", () -> {
								throw t;
							});
						}
						return DynamicTest.dynamicTest(displayName, () -> {
							List<Object> errors = new ArrayList<>();

							try {
								// measure
								var measuredSize = serializer.measure(data);
								boolean doSizeCheck = true;
								if (measuredSize < 0) {
									errors.add("measured size is negative: " + measuredSize);
									measuredSize = 64;
									doSizeCheck = false;
								} else if (measuredSize > 0xFFFF) {
									errors.add("measured size more than the test max of 0xFFFF: " + measuredSize);
									measuredSize = 0xFFFF;
									doSizeCheck = false;
								}

								// adding extra room
								var io = ByteBufferIO.create((measuredSize + 64) * 4);

								// put
								serializer.put(io, data);

								// measure check
								final int writtenSize = io.pos();

								// rewind to 0
								io.rewind();

								// get
								var dataOut = serializer.get(io);

								// read check
								final int readSize = io.pos();
								if (doSizeCheck) {
									switch ((readSize != writtenSize ? 4 : 0) + (readSize != measuredSize ? 2 : 0) + (measuredSize != writtenSize ? 1 : 0)) {
										case 0b000 -> {
										}
										case 0b011 -> { // measure is wrong
											if (measuredSize < readSize)
												errors.add("Measured size is too small. " + measuredSize + " < " + readSize);
											else
												errors.add("Measured size is too big. " + measuredSize + " > " + readSize);
										}
										case 0b101 -> { // written size is wrong
											if (writtenSize < readSize)
												errors.add("Written size is too small. " + writtenSize + " < " + readSize);
											else
												errors.add("Written size is too big. " + writtenSize + " > " + readSize);
										}
										case 0b110 -> { // measure is wrong
											if (readSize < writtenSize)
												errors.add("Read size is too small. " + readSize + " < " + writtenSize);
											else
												errors.add("Read size is too big. " + readSize + " > " + writtenSize);
										}
										case 0b111 -> { // all 3 differ
											errors.add("All sizes are different!");
											errors.add("\tMeasure: " + measuredSize);
											errors.add("\tWrite: " + writtenSize);
											errors.add("\tRead: " + readSize);
										}
										default -> {
											errors.add(new AssertionError("Mathematical unreachable code reached"));
										}
									}
								} else {
									if (writtenSize < readSize)
										errors.add("Written size is smaller than read size. " + writtenSize + " < " + readSize);
									else if (writtenSize > readSize)
										errors.add("Written size is bigger than read size. " + writtenSize + " > " + readSize);
									else
										errors.add("Actual size = " + writtenSize);
								}

								// result check
								if (!data.equals(dataOut))
									errors.add(new AssertionFailedError("Objects do not match\n" + data + "\n != \n" + dataOut, data, dataOut));
							} catch (Throwable t) {
								errors.add(t);
							}

							if (!errors.isEmpty()) {
								// errors happened
								if (errors.size() == 1 && errors.get(0) instanceof Throwable t)
									throw t;

								String msg = errors.stream().map(Object::toString).collect(Collectors.joining("\n"));

								AssertionError error = new AssertionError(msg);

								for (Object o : errors)
									if (o instanceof Throwable t)
										error.addSuppressed(t);

								throw error;
							}
						});
					}));
		} catch (Throwable t) {
			return DynamicTest.dynamicTest(clazz.getSimpleName(), () -> {
				if (clazz.isAnnotationPresent(FailTest.class) && !"Expected test to fail ==> expected: <false> but was: <true>".equals(t.getMessage())) {
					t.printStackTrace();
					FailTest failTest = clazz.getDeclaredAnnotation(FailTest.class);
					if (failTest.value() != Throwable.class)
						Assertions.assertTrue(failTest.value().isInstance(t), "Expected a different error type: " +
								failTest.value().getSimpleName() + ", got " + t.getClass().getSimpleName());
					if (!failTest.msg().isEmpty())
						Assertions.assertEquals(failTest.msg(), t.getMessage(), "Expected a different error msg");
				} else
					throw t;
			});
		}
	}
}
