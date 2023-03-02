package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.simple.GetterTest;
import dev.quantumfusion.hyphen.scan.simple.arrays.IntArrayTest;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AlphasSerializer {

	@org.junit.jupiter.api.Test
	void name() {
		final String[] thing2 = new String[]{"fg", "fdas"};
		final List<String> fdas = List.of("fdas", "fdasfas", "fdsafdsfsdf", "fdsafsadfadsfsd", "fdass", "faserq");
		IntArrayTest shader = new IntArrayTest(new int[]{4,3,3,3,3});
		test(new Test(new int[][]{
				{24, 24},
				{456, 456},
				{645, 645},
				{243, 243},
		}));
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
		public int[][] hi;

		public Test(int[][] hi) {
			this.hi = hi;
		}
	}

}
