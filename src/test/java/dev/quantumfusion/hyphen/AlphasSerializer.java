package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.DataFixedArraySize;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.test.poly.classes.c.C1;
import dev.quantumfusion.hyphen.test.poly.classes.c.CoWrappedC1;
import dev.quantumfusion.hyphen.test.poly.classes.c.IntC1;
import dev.quantumfusion.hyphen.test.poly.classes.c.WrappedC1;
import dev.quantumfusion.hyphen.test.poly.classes.pair.Pair;
import dev.quantumfusion.hyphen.test.poly.classes.pair.SelfPair;
import dev.quantumfusion.hyphen.test.poly.wildcards.IntC1PartialError;
import dev.quantumfusion.hyphen.test.simple.arrays.IntArrayTest;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AlphasSerializer {

	@org.junit.jupiter.api.Test
	void name() {
		final String[] thing2 = new String[]{"fg", "fdas"};
		final List<String> fdas = List.of("fdas", "fdasfas", "fdsafdsfsdf", "fdsafsadfadsfsd", "fdass", "faserq");
		IntArrayTest shader = new IntArrayTest(new int[]{4,3,3,3,3});
		test(new Test(new Test2<>(234, new ArrayList<>())));
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
		public Test2<?, List<Integer>> data;

		public Test(Test2<?, List<Integer>> data) {
			this.data = data;
		}
	}

	public static class Test2<O, V extends List<O>> {
		public O o;
		public V v;

		public Test2(O o, V v) {
			this.o = o;
			this.v = v;
		}
	}

}
