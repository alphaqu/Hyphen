package dev.notalpha.hyphen;

import dev.notalpha.hyphen.io.ByteBufferIO;
import dev.notalpha.hyphen.scan.annotations.DataNullable;
import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C0;
import dev.notalpha.hyphen.test.poly.classes.c.CM1;
import dev.notalpha.hyphen.test.poly.classes.c.IntC1;
import dev.notalpha.hyphen.test.poly.classes.c.enums.EnumC;
import dev.notalpha.hyphen.test.poly.classes.c.enums.EnumCBoolean;
import dev.notalpha.hyphen.test.poly.classes.c.enums.EnumCSingleton;
import dev.notalpha.hyphen.test.simple.arrays.IntArrayTest;

import java.nio.file.Path;
import java.util.List;

public class AlphasSerializer {

	@org.junit.jupiter.api.Test
	void name() {
		final String[] thing2 = new String[]{"fg", "fdas"};
		final List<String> fdas = List.of("fdas", "fdasfas", "fdsafdsfsdf", "fdsafsadfadsfsd", "fdass", "faserq");
		IntArrayTest shader = new IntArrayTest(new int[]{4,3,3,3,3});
		test(new Test(EnumC.A));
	}

	public static <O> void test(O data) {
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, (Class<O>) data.getClass());
		factory.setExportDir(Path.of("./"));
		factory.setOption(Options.FAST_ALLOC, false);
		final HyphenSerializer<ByteBufferIO, O> serializer = factory.build();
		for (int i = 0; i < 20; i++) {
			try {
				long time = System.nanoTime();
				final int measure = (int) serializer.measure(data);
				final ByteBufferIO unsafeIO = ByteBufferIO.create(measure);
				serializer.put(unsafeIO, data);
				unsafeIO.rewind();
				final O test = serializer.get(unsafeIO);
				System.out.println(test);

				System.out.println((System.nanoTime() - time));
			} catch (Throwable error) {
				error.printStackTrace();
			}
		}

	}
	public static class Test {
		@DataSubclasses({EnumC.class, EnumCBoolean.class, EnumCSingleton.class, C0.class, IntC1.class})
		@DataNullable
		public CM1 data;

		public Test(@DataSubclasses({EnumC.class, EnumCBoolean.class, EnumCSingleton.class, C0.class, IntC1.class}) @DataNullable CM1 data) {
			this.data = data;
		}
	}

}
