package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.UnsafeIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;

import java.nio.file.Path;

public class AlphasSerializer {

	@org.junit.jupiter.api.Test
	void name() {
		RecursiveTest data = new RecursiveTest(new RecursiveTest2("fads"));
		test(data);
	}

	public static <O> void test(O data) {
		var factory = SerializerFactory.createDebug(UnsafeIO.class, (Class<O>) data.getClass());
		factory.setExportDir(Path.of("./"));
		factory.setOption(Options.FAST_ALLOC, false);
		try {
			final HyphenSerializer<UnsafeIO, O> serializer = factory.build();

			final int measure = serializer.measure(data);
			final UnsafeIO unsafeIO = UnsafeIO.create(measure);
			serializer.put(unsafeIO, data);
			System.out.println(unsafeIO.pos() + " / " + measure);
			unsafeIO.rewind();
			final O test = serializer.get(unsafeIO);
			System.out.println(test.equals(data));
			System.out.println(data);
			System.out.println(test);
		} catch (Throwable error) {
			error.printStackTrace();
		}

	}


	public interface RecursiveInterface {

	}

	@Data
	@DataNullable
	public static class RecursiveTest implements RecursiveInterface {
		public final @DataSubclasses({RecursiveTest.class, RecursiveTest2.class}) RecursiveInterface test;

		public RecursiveTest(RecursiveInterface test) {
			this.test = test;
		}
	}

	@Data
	@DataNullable
	public static class RecursiveTest2 implements RecursiveInterface {
		public final String test2;

		public RecursiveTest2(String test2) {
			this.test2 = test2;
		}
	}
}
