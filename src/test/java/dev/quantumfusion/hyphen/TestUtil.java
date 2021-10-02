package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.poly.DoubleC1Pain;
import dev.quantumfusion.hyphen.scan.poly.RecursiveInteger;
import dev.quantumfusion.hyphen.scan.poly.RecursiveString;
import dev.quantumfusion.hyphen.scan.poly.C1OfC1;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.thr.exception.NotYetImplementedException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

public class TestUtil {

	public static Iterator<DynamicNode> testPackage(String path) {

		try {
			String packageName = "dev.quantumfusion.hyphen." + path;

			var packagePath = Path.of(ClassLoader.getSystemClassLoader().getResource(packageName.replaceAll("[.]", "/")).toURI());


			List<List<DynamicNode>> nodes = new ArrayList<>(List.of(new ArrayList<>()));

			Files.walkFileTree(packagePath, new FileVisitor<>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
					if (dir.endsWith("classes")) {
						// skip classes subfolders
						return FileVisitResult.SKIP_SUBTREE;
					}

					nodes.add(new ArrayList<>());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					if (file.getFileName().toString().endsWith(".class")) {
						nodes.get(nodes.size() - 1).add(test(TestUtil.getClass(packagePath.relativize(file), packageName)));
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
					int size = nodes.size();
					nodes.get(size - 2).add(DynamicContainer.dynamicContainer(dir.getFileName().toString(), nodes.get(size - 1)));
					nodes.remove(size - 1);
					return FileVisitResult.CONTINUE;
				}
			});
			return nodes.get(0).iterator();
		} catch (IOException | URISyntaxException e) {
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
					run(clazz);
				} catch (Throwable throwable) {
					if (throwable.getClass().equals(value)) {
						System.err.println("Got expected error: ");
						throwable.printStackTrace();
						Assumptions.assumeTrue(value != NotYetImplementedException.class, "Ignoring NYI feature");
						return;
					}
					fail("Expected a different exception: " + value.getSimpleName(), throwable);
				}

				if (value == Throwable.class) {
					fail("Forcefully failed test");
				} else {
					fail("Expected test to fail");
				}
			};
		} else {
			executable = () -> run(clazz);
		}

		return DynamicTest.dynamicTest(clazz.getSimpleName(), URI.create("class:" + clazz.getName()), executable);
	}

	private static HyphenSerializer<?, ByteBufferIO> run(Class<?> clazz) {
		final HyphenSerializer<?, ByteBufferIO> build = SerializerFactory.createDebug(clazz).build(ByteBufferIO.class);

		return build;
	}

	public static void main(String[] args) {/*
		final SerializerFactory<C1OfC1> debug = SerializerFactory.createDebug(C1OfC1.class);

		final HyphenSerializer<C1OfC1, ByteBufferIO> build = debug.build(ByteBufferIO.class);
		final C1OfC1 encode = new C1OfC1(new C1<>(new C1<>(3)));
		// final Recursive encode = new Recursive(new C1<>(""));
		final int measure = (int) build.measure(encode);
		final ByteBufferIO direct = ByteBufferIO.createDirect(measure * 100);
		build.encode(direct, encode);
		direct.rewind();
		final C1OfC1 decode = build.decode(direct);

		System.out.println("Measured: " + measure);
		System.out.println("Actual: " + direct.pos());
		System.out.println(decode.equals(encode));
*/
	}

	@TestFactory
	Stream<DynamicNode> name() {
		return createGeneratedTests(RecursiveString.class, ByteBufferIO.class, ByteBufferIO::createDirect, RecursiveString.generate(3));
	}
	@TestFactory
	Stream<DynamicNode> name2() {
		return createGeneratedTests(RecursiveInteger.class, ByteBufferIO.class, ByteBufferIO::createDirect, RecursiveInteger.generate(3));
	}

	@TestFactory
	Stream<DynamicNode> name3() {
		return createGeneratedTests(DoubleC1Pain.class, ByteBufferIO.class, ByteBufferIO::createDirect, DoubleC1Pain.generate()).limit(100);
	}

	private static <DATA, IO extends ByteBufferIO> Stream<DynamicNode> createGeneratedTests(Class<DATA> testClass, Class<IO> io, IntFunction<IO> ioCreator, Stream<? extends DATA> objects) {
		final SerializerFactory<DATA> debug = SerializerFactory.createDebug(testClass);
		final HyphenSerializer<DATA, IO> build = debug.build(io);
		return objects.map(encode -> DynamicTest.dynamicTest( "" + encode, () -> {
			final int measure = (int) build.measure(encode);
			// make oversized
			final IO direct = ioCreator.apply((measure + 16) * 4);
			build.encode(direct, encode);
			direct.rewind();
			final DATA decode = build.decode(direct);

			// System.out.println("Measured: " + measure);
			// System.out.println("Actual: " + direct.pos());
			// System.out.println(decode.equals(encode));
			Assertions.assertEquals(measure, direct.pos(), "Expected the read sizes to match");
			Assertions.assertEquals(encode, decode, "Expected the return to equal the input");

		}));
	}

	private static Class<?> getClass(Path className, String packageName) {
		try {
			String fileName = className.getFileName().toString();
			StringJoiner sj = new StringJoiner(".");

			sj.add(packageName);

			Path parent = className.getParent();
			if (parent != null) {
				for (Path path : parent) {
					sj.add(path.toString());
				}
			}

			sj.add(fileName.substring(0, fileName.lastIndexOf('.')));

			return Class.forName(sj.toString());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
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
