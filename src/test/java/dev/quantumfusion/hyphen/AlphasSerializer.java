package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.simple.arrays.IntArrayTest;

import java.nio.file.Path;
import java.util.List;

public class AlphasSerializer {

	@org.junit.jupiter.api.Test
	void name() {
		final String[] thing2 = new String[]{"fg", "fdas"};
		final List<String> fdas = List.of("fdas", "fdasfas", "fdsafdsfsdf", "fdsafsadfadsfsd", "fdass", "faserq");
		IntArrayTest shader = new IntArrayTest(new int[]{4,3,3,3,3});
		test(shader);
	}

	public static <O> void test(O data) {
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, (Class<O>) data.getClass());
		factory.setExportDir(Path.of("./"));
		factory.setOption(Options.FAST_ALLOC, false);
		final HyphenSerializer<ByteBufferIO, O> serializer = factory.build();
		for (int i = 0; i < 20; i++) {
			try {
				long time = System.nanoTime();
				final int measure = serializer.measure(data);
				final ByteBufferIO unsafeIO = ByteBufferIO.create(measure);
				serializer.put(unsafeIO, data);
				unsafeIO.rewind();
				final O test = serializer.get(unsafeIO);
				System.out.println((System.nanoTime() - time));
			} catch (Throwable error) {
				error.printStackTrace();
			}
		}

	}

	@Data
	public static class DashShader {
		public final List<String> thing;
		public final List<String> thing1;


		public DashShader(List<String> thing, List<String> thing1) {
			this.thing = thing;
			this.thing1 = thing1;
		}
	}
}
